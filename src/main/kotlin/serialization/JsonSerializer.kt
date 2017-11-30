package serialization

import game.*
import js.Object
import util.IntVector2

private class JsonSaver {
    private val objectRefs = mutableMapOf<Serializable, Int>()
    private val objectList = mutableListOf<String>()

    private fun obj(vararg vals: Pair<String, String>): String =
            vals.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, value) -> "\"$key\":$value" }

    private inline fun <T> saveNullable(t: T?, generator: (T) -> String) = if (t == null) "null" else generator(t)

    private fun saveLocation(loc: IntVector2) = obj("x" to loc.x.toString(), "y" to loc.y.toString())

    private fun <T> saveList(list: List<T>, mapper: ((T) -> String)? = null): String =
            list.joinToString(separator = ",", prefix = "[", postfix = "]", transform = mapper)

    private fun saveNumber(number: Number) = number.toString()
    private fun saveString(string: String) = "\"$string\""
    private fun saveEnum(value: Enum<*>) = "\"${value.name}\""

    private fun <T : Serializable> saveReference(obj: T, generator: (T) -> String): String {
        if (obj !in objectRefs) {
            val ser = generator(obj)
            objectRefs[obj] = objectList.size
            objectList.add(ser)
        }
        return objectRefs[obj]!!.toString()
    }

    private fun <T : Serializable> saveReferenceList(list: List<T>, generator: (T) -> String): String =
            saveList(list) { saveReference(it, generator) }

    fun save(game: ExodusGame): String {
        val root = obj(
                "galaxy" to saveReference(game.galaxy, this::save),
                "fleet" to saveReference(game.fleet, this::save),
                "day" to saveNumber(game.day)
        )
        val objs = saveList(objectList)
        return obj(
                "root" to root,
                "objs" to objs
        )
    }

    private fun save(galaxy: Galaxy): String = obj(
            "mapSize" to saveNumber(galaxy.mapSize),
            "stars" to saveReferenceList(galaxy.stars, this::save)
    )

    private fun save(star: StarSystem): String = obj(
            "name" to saveString(star.name),
            "type" to saveEnum(star.type),
            "location" to saveLocation(star.location),
            "planets" to saveReferenceList(star.planets, this::save)
    )

    private fun save(planet: Planet): String = obj(
            "name" to saveString(planet.name),
            "type" to saveEnum(planet.type),
            "features" to saveList(planet.features, this::saveEnum),
            "exploration" to saveNumber(planet.exploration)
    )

    private fun save(fleet: Fleet): String = obj(
            "location" to saveLocation(fleet.location),
            "destination" to saveLocation(fleet.destination),
            "ships" to saveReferenceList(fleet.ships.toList(), this::save)
    )

    private fun save(ship: Ship): String = obj(
            "name" to saveString(ship.name),
            "shipClass" to saveEnum(ship.shipClass),
            "hullPoints" to saveNumber(ship.hullPoints),
            "crew" to saveNumber(ship.crew),
            "inventory" to saveReference(ship.inventory, this::save),
            "exploring" to saveNullable(ship.exploring) { saveReference(it, this::save) },
            "mining" to saveNullable(ship.mining) { obj(
                    "planet" to saveReference(it.planet, this::save),
                    "resource" to saveEnum(it.resource)
            ) }
    )

    private fun save(inventory: Inventory): String = obj(
            "capacity" to saveNumber(inventory.capacity),
            "contents" to obj(*inventory.asMap().map { (k, v) -> k.name to saveNumber(v) }.toTypedArray())
    )
}

private class JsonLoader(string: String) {
    val root: dynamic
    val objs: Array<dynamic>

    val objectCache = mutableMapOf<Int, Serializable>()

    init {
        val obj: dynamic = JSON.parse(string)
        root = obj.root
        objs = obj.objs.unsafeCast<Array<dynamic>>()
    }

    private inline fun <reified T> checkUndef(obj: dynamic, loader: (dynamic) -> T): T {
        if (obj === undefined) {
            throw SerializationException("Undefined property; expecting a ${T::class.simpleName}")
        }
        return loader(obj)
    }

    private inline fun <reified T> loadNullable(obj: dynamic, loader: (dynamic) -> T): T? = checkUndef(obj) {
        if (obj === null) null else loader(obj)
    }

    private fun loadInt(obj: dynamic) = checkUndef(obj) { (obj as Number).toInt() }
    private fun loadString(obj: dynamic) = checkUndef(obj) { obj as String }

    private inline fun <T> loadList(arr: dynamic, loader: (dynamic) -> T): List<T> = checkUndef(arr) {
        (arr as Array<dynamic>).map(loader)
    }

    private fun loadLocation(obj: dynamic) = checkUndef(obj) {
        val x = loadInt(obj.x)
        val y = loadInt(obj.y)
        IntVector2(x, y)
    }

    private inline fun <T : Serializable> loadReference(ref: dynamic, loader: (dynamic) -> T): T {
        if (ref !is Int) throw SerializationException("Expected a reference")
        return objectCache.getOrPut(ref) { loader(objs[ref]) }.unsafeCast<T>()
    }

    private inline fun <T : Serializable> loadReferenceList(arr: dynamic, loader: (dynamic) -> T) = checkUndef(arr) {
        (arr as Array<dynamic>).map { loadReference(it, loader) }
    }

    fun load(): ExodusGame {
        val galaxy = loadReference(root.galaxy) { loadGalaxy(it) }
        val fleet = loadReference(root.fleet) { loadFleet(it) }
        val day = loadInt(root.day)
        return ExodusGame(galaxy, fleet, day)
    }

    private fun loadGalaxy(obj: dynamic) = checkUndef(obj) {
        val stars = loadReferenceList(obj.stars, this::loadStar)
        val mapSize = loadInt(obj.mapSize)
        Galaxy(stars, mapSize)
    }

    private fun loadStar(obj: dynamic) = checkUndef(obj) {
        val starName = loadString(obj.name)
        val location = loadLocation(obj.location)
        val type = StarType.valueOf(loadString(obj.type))
        val planets = loadReferenceList(obj.planets, this::loadPlanet)
        StarSystem(starName, location, type, planets)
    }

    private fun loadPlanet(obj: dynamic) = checkUndef(obj) {
        val planetName = loadString(obj.name)
        val type = PlanetType.valueOf(loadString(obj.type))
        val features = loadList(obj.features) { PlanetFeature.valueOf(loadString(it)) }
        val exploration = loadInt(obj.exploration)
        Planet(planetName, type, features, exploration)
    }

    private fun loadFleet(obj: dynamic) = checkUndef(obj) {
        val location = loadLocation(obj.location)
        val destination = loadLocation(obj.destination)
        val ships = loadReferenceList(obj.ships, this::loadShip)
        Fleet(ships, location, destination)
    }

    private fun loadShip(obj: dynamic) = checkUndef(obj) {
        val shipName = loadString(obj.name)
        val shipClass = ShipClass.valueOf(loadString(obj.shipClass))
        val hull = loadInt(obj.hullPoints)
        val crew = loadInt(obj.crew)
        val inv = loadReference(obj.inventory, this::loadInventory)
        val exploring = loadNullable(obj.exploring) { loadReference(it, this::loadPlanet) }
        val mining = loadNullable(obj.mining) { Ship.MiningTarget(
                loadReference(it.planet, this::loadPlanet),
                InventoryItem.valueOf(loadString(it.resource))
        ) }
        Ship(shipName, shipClass, hull, crew, inv, exploring, mining)
    }

    private fun loadInventory(obj: dynamic) = checkUndef(obj) {
        val capacity = loadInt(obj.capacity)
        val contents = Object.keys(obj.contents).associateBy(InventoryItem::valueOf, { (obj.contents[it] as Number).toInt() })
        Inventory(capacity, contents)
    }
}

object JsonSerializer : Serializer<ExodusGame, String> {
    override fun save(model: ExodusGame) = JsonSaver().save(model)

    override fun load(data: String) = JsonLoader(data).load()
}
