package serialization

import game.*
import serialization.SerializationModels.SGame
import util.Location

object JsonSerializer {
    private fun obj(vararg vals: Pair<String, String>): String =
            vals.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, value) -> "\"$key\":$value" }

    fun saveGame(game: SGame): String = obj(
            "galaxy" to saveGalaxy(game.galaxy),
            "fleet" to saveFleet(game.fleet),
            "day" to saveNumber(game.day)
    )

    private fun saveGalaxy(galaxy: SerializationModels.SGalaxy): String = obj(
            "mapSize" to saveNumber(galaxy.mapSize),
            "stars" to saveList(galaxy.stars, this::saveStar)
    )

    private fun saveStar(star: SerializationModels.SStarSystem): String = obj(
            "name" to saveString(star.name),
            "type" to saveEnum(star.type),
            "location" to saveLocation(star.location),
            "planets" to saveList(star.planets, this::savePlanet)
    )

    private fun savePlanet(planet: SerializationModels.SPlanet): String = obj(
            "name" to saveString(planet.name),
            "type" to saveEnum(planet.type),
            "features" to saveList(planet.features, this::saveEnum),
            "exploration" to saveNumber(planet.exploration)
    )

    private fun saveFleet(fleet: SerializationModels.SFleet): String = obj(
            "location" to saveLocation(fleet.location),
            "ships" to saveList(fleet.ships, this::saveShip)
    )

    private fun saveShip(ship: SerializationModels.SShip): String = obj(
            "name" to saveString(ship.name),
            "shipClass" to saveEnum(ship.shipClass),
            "hullPoints" to saveNumber(ship.hullPoints),
            "crew" to saveNumber(ship.crew),
            "inventory" to saveInventory(ship.inventory)
    )

    private fun saveInventory(inventory: SerializationModels.SInventory): String = obj(
            "capacity" to saveNumber(inventory.capacity),
            "contents" to obj(*inventory.contents.map { (k, v) -> k.name to saveNumber(v) }.toTypedArray())
    )

    private fun saveLocation(loc: Location) = obj("x" to loc.x.toString(), "y" to loc.y.toString())

    private fun <T> saveList(list: List<T>, mapper: (T) -> String): String =
            list.joinToString(separator = ",", prefix = "[", postfix = "]", transform = mapper)

    private fun saveNumber(number: Number) = number.toString()
    private fun saveString(string: String) = "\"$string\""
    private fun saveEnum(value: Enum<*>) = "\"${value.name}\""

    fun loadGame(string: String): SGame {
        val obj: dynamic = JSON.parse(string)
        val galaxy = loadGalaxy(obj.galaxy, "game.galaxy")
        val fleet = loadFleet(obj.fleet, "game.fleet")
        val day = loadInt(obj.day, "game.day")
        return SGame(galaxy, fleet, day)
    }

    private fun loadGalaxy(obj: dynamic, name: String): SerializationModels.SGalaxy {
        checkUndef(obj, name)
        val stars = loadList(obj.stars, "$name.stars", this::loadStar)
        val mapSize = loadInt(obj.mapSize, "$name.mapSize")
        return SerializationModels.SGalaxy(stars, mapSize)
    }

    private fun loadStar(obj: dynamic, name: String): SerializationModels.SStarSystem {
        checkUndef(obj, name)
        val starName = loadString(obj.name, "$name.name")
        val location = loadLocation(obj.location, "$name.location")
        val type = StarType.valueOf(loadString(obj.type, "$name.type"))
        val planets = loadList(obj.planets, "$name.planets", this::loadPlanet)
        return SerializationModels.SStarSystem(starName, location, type, planets)
    }

    private fun loadPlanet(obj: dynamic, name: String): SerializationModels.SPlanet {
        checkUndef(obj, name)
        val planetName = loadString(obj.name, "$name.name")
        val type = PlanetType.valueOf(loadString(obj.type, "$name.type"))
        val features = loadList(obj.features, "$name.features") { o, n -> PlanetFeature.valueOf(loadString(o, n)) }
        val exploration = loadInt(obj.exploration, "$name.exploration")
        return SerializationModels.SPlanet(planetName, type, features, exploration)
    }

    private fun loadFleet(obj: dynamic, name: String): SerializationModels.SFleet {
        checkUndef(obj, name)
        val location = loadLocation(obj.location, "$name.location")
        val ships = loadList(obj.ships, "$name.ships", this::loadShip)
        return SerializationModels.SFleet(ships, location)
    }

    private fun loadShip(obj: dynamic, name: String): SerializationModels.SShip {
        checkUndef(obj, name)
        val shipName = loadString(obj.name, "$name.name")
        val shipClass = ShipClass.valueOf(loadString(obj.shipClass, "$name.shipClass"))
        val hull = loadInt(obj.hullPoints, "$name.hullPoints")
        val crew = loadInt(obj.crew, "$name.crew")
        val inv = loadInventory(obj.inventory, "$name.inventory")
        return SerializationModels.SShip(shipName, shipClass, hull, crew, inv)
    }

    private fun loadInventory(obj: dynamic, name: String): SerializationModels.SInventory {
        checkUndef(obj, name)
        val capacity = loadInt(obj.capacity, "$name.capacity")
        val contents = mapOf<InventoryItem, Int>() // TODO
        return SerializationModels.SInventory(capacity, contents)
    }

    private fun loadLocation(obj: dynamic, name: String): Location {
        checkUndef(obj, name)
        val x = loadInt(obj.x, name + ".x")
        val y = loadInt(obj.y, name + ".y")
        return Location(x, y)
    }

    private inline fun <T> loadList(arr: dynamic, name: String, mapper: (dynamic, String) -> T): List<T> {
        checkUndef(arr, name)
        return (arr as Array<dynamic>).mapIndexed { i, e -> mapper(e, "$name[$i]") }
    }

    private fun loadString(obj: dynamic, name: String): String {
        checkUndef(obj, name)
        return obj as String
    }

    private fun loadInt(obj: dynamic, name: String): Int {
        checkUndef(obj, name)
        return (obj as Number).toInt()
    }

    private fun checkUndef(obj: dynamic, name: String) {
        if (obj == undefined) {
            throw SerializationException("Undefined property: $name")
        }
    }
}
