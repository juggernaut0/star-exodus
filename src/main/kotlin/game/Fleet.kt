package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

class Fleet(
        ships: Collection<Ship>,
        location: IntVector2,
        var destination: IntVector2,
        discoveredStars: Collection<StarSystem>
) : EventEmitter<Fleet>() {
    var location: IntVector2 = location
        private set

    private val _ships = ships.toMutableList()
    val ships: Collection<Ship> get() = _ships

    private val _discovered = discoveredStars.toMutableSet()
    val discoveredStars: Collection<StarSystem> get() = _discovered

    val speed: Int get() = ships.asSequence().map { it.shipClass.speed }.min() ?: 0

    val onArrive = Event<Fleet, StarSystem>().bind(this)

    fun doTurn(game: ExodusGame) {
        abandonUncrewed()
        growFood()
        eatFood()
        ships.forEach { it.births(); it.deaths() }

        val moved = moveTowardsDestination()
        if (moved) {
            _discovered.addAll(game.galaxy.getNearbyStars(location, SENSOR_RANGE))

            for (ship in ships) {
                ship.exploring = null
                ship.mining = null
            }
        }

        ships.forEach { it.repair() }

        val currentStar = game.galaxy.getStarAt(location)
        if (currentStar != null) {
            if (moved) {
                SystemArrival.execute(this, currentStar)
                onArrive(currentStar)
            } else {
                exploreSystem(currentStar)
                ships.forEach { it.mine() }
            }
        }
    }

    fun abandonShip(ship: Ship) {
        _ships.remove(ship)
    }

    private fun abandonUncrewed() {
        val uncrewed = _ships.filter { it.crew < it.shipClass.minCrew }
        var remaining = uncrewed.sumBy { it.crew }
        _ships.removeAll(uncrewed)

        val notFull = _ships.filter { it.crew < it.shipClass.maxCrew }.toMutableList()
        while (remaining > 0) {
            if (notFull.size == 0) break

            val ship = Random.choice(notFull)
            ship.modCrew(1)
            if (ship.crew == ship.shipClass.maxCrew) {
                notFull.remove(ship)
            }
            remaining--
        }
    }

    private fun growFood() {
        ships.forEach { it.inventory.addItems(InventoryItem.FOOD, it.shipClass.foodProduction) }
    }

    private fun eatFood() {
        ships.forEach { it.eatFood() }
    }

    fun fuelConsumptionAtSpeed(ship: Ship, dist: Double = speed.toDouble()) = ceil(ship.fuelConsumption * dist).toInt()

    private fun moveTowardsDestination(): Boolean {
        if (destination == location) return false

        val dist = IntVector2.distance(destination, location)
        if (dist <= speed) {
            location = destination
        } else {
            location += (destination - location) * (speed / dist)
        }
        val fuelNeeded = _ships.associate { it to fuelConsumptionAtSpeed(it, min(speed.toDouble(), dist)) }
        _ships.removeAll { it.inventory[InventoryItem.FUEL] < fuelNeeded[it]!! }
        _ships.forEach { it.inventory.removeItems(InventoryItem.FUEL, fuelNeeded[it]!!) }

        return true
    }

    private fun exploreSystem(star: StarSystem) {
        val explorers = ships.groupBy { it.exploring }
        star.planets.forEach { planet -> planet.explore(explorers[planet] ?: emptyList()) }
    }

    fun autoSupply() {
        autoSupply(InventoryItem.FUEL) { fuelConsumptionAtSpeed(it) }
        autoSupply(InventoryItem.FOOD) { it.foodConsumption }
    }

    private fun autoSupply(item: InventoryItem, consFn: (Ship) -> Int) {
        val total = ships.sumBy { it.inventory[item] }
        val totalCons = ships.sumBy(consFn)

        val days = ceil(total.toDouble() / totalCons).toInt()

        fun shipsToSurplus() = ships.asSequence().map{ it to it.inventory[item] - consFn(it) * days }
        fun has() = shipsToSurplus().find { it.second > 0 }
        fun needs() = shipsToSurplus().find { it.second < 0 && it.first.inventory.freeSpace > 0 }

        var surplus = has()
        var deficit = needs()
        while (surplus != null && deficit != null) {
            val amt = min(surplus.second, -deficit.second)
            surplus.first.inventory.transferItemsTo(deficit.first.inventory, item, amt)
            surplus = has()
            deficit = needs()
        }
    }

    companion object {
        const val SENSOR_RANGE = 600.0

        operator fun invoke(numShips: Int, shipNames: List<String>, startingLocation: IntVector2, nearbyStars: Collection<StarSystem>): Fleet {
            val home = WeightedList(
                    ShipClass.SMALL_COLONY_SHIP to 6,
                    ShipClass.LARGE_COLONY_SHIP to 9,
                    ShipClass.LIVESHIP to 4,
                    ShipClass.CITYSHIP to 1
            )

            val capital = WeightedList(
                    ShipClass.BATTLESHIP to 7,
                    ShipClass.DREADNOUGHT to 6,
                    ShipClass.FLEET_CARRIER to 3,
                    ShipClass.TITAN to 3,
                    ShipClass.BATTLECARRIER to 1
            )

            val escort = WeightedList(
                    ShipClass.CORVETTE to 20,
                    ShipClass.SCOUT to 14,
                    ShipClass.DESTROYER to 16,
                    ShipClass.TROOP_CARRIER to 4,
                    ShipClass.FRIGATE to 8,
                    ShipClass.CRUISER to 6,
                    ShipClass.HEAVY_CRUISER to 6,
                    ShipClass.CARRIER to 4
            )

            val civilian = WeightedList(
                    ShipClass.SMALL_PASSENGER_CARRIER to 12,
                    ShipClass.MEDIUM_PASSENGER_CARRIER to 10,
                    ShipClass.LARGE_PASSENGER_CARRIER to 8,
                    ShipClass.HUGE_PASSENGER_CARRIER to 6,
                    ShipClass.CRUISE_LINER to 4,
                    ShipClass.DREAM_LINER to 2,
                    ShipClass.SMALL_FREIGHT_CARRIER to 8,
                    ShipClass.MEDIUM_FREIGHT_CARRIER to 6,
                    ShipClass.LARGE_FREIGHT_CARRIER to 4,
                    ShipClass.HUGE_FREIGHT_CARRIER to 2,
                    ShipClass.SUPER_FREIGHT_CARRIER to 1,
                    ShipClass.REFINERY_SHIP to 3,
                    ShipClass.FUEL_TANKER to 4,
                    ShipClass.MOBILE_DRY_DOCK to 1,
                    ShipClass.MINING_SHIP to 3
            )

            val numEscort = ((numShips - 2) * Random.range(0.2, 0.5)).roundToInt()
            val numCivilian = numShips - 2 - numEscort

            val classes = listOf(
                    Random.choice(home),
                    Random.choice(capital),
                    *Random.sample(escort, numEscort).toTypedArray(),
                    *Random.sample(civilian, numCivilian).toTypedArray()
            )

            val ships = Random.sample(shipNames, numShips)
                    .zip(classes)
                    .mapTo(mutableListOf()) { (name, cls) -> Ship(name, cls) }

            return Fleet(ships, startingLocation, startingLocation, nearbyStars)
        }
    }

    object Serial : Serializer<Fleet, Serial.Data> {
        @Serializable
        class Data(val ships: List<Int>,
                   val location: IntVector2,
                   val destination: IntVector2,
                   val discoveredStars: Collection<Int>)

        override fun save(model: Fleet, refs: RefSaver): Data {
            return Data(model.ships.map { refs.saveShipRef(it) }, model.location, model.destination, model.discoveredStars.map { refs.saveStarRef(it) })
        }

        override fun load(data: Data, refs: RefLoader): Fleet {
            return Fleet(data.ships.map { refs.loadShipRef(it) }, data.location, data.destination, data.discoveredStars.map { refs.loadStarRef(it) })
        }
    }
}
