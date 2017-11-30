package game

import serialization.Serializable
import util.*
import kotlin.math.ceil
import kotlin.math.min

class Fleet(ships: Collection<Ship>, location: IntVector2, var destination: IntVector2) : Serializable, EventEmitter<Fleet>() {
    var location: IntVector2 = location
        private set

    private val _ships = ships.toMutableList()
    val ships: Collection<Ship> get() = _ships

    val speed: Int get() = ships.asSequence().map { it.shipClass.speed }.min() ?: 0

    val onArrive = Event<Fleet, StarSystem>().bind(this)

    fun doTurn(game: ExodusGame) {
        abandonUncrewed()
        growFood()
        eatFood()
        val moved = moveTowardsDestination()
        if (moved) {
            for (ship in ships) {
                ship.exploring = null
                ship.mining = null
            }
        }

        val currentStar = game.galaxy.getStarAt(location)
        if (currentStar != null) {
            exploreSystem(currentStar)
            ships.forEach { it.mine() }
        }

        if (moved && currentStar != null) {
            onArrive(currentStar)
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

        // TODO discovered systems (600 range?)

        return true
    }

    private fun exploreSystem(star: StarSystem) {
        val explorers = ships.groupBy { it.exploring }
        star.planets.forEach { planet -> planet.explore(explorers[planet] ?: emptyList()) }
    }

    fun autoSupply() {
        autoSupply(InventoryItem.FOOD) { it.foodConsumption }
        autoSupply(InventoryItem.FUEL) { fuelConsumptionAtSpeed(it) }
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
        operator fun invoke(numShips: Int, shipNames: List<String>, startingLocation: IntVector2): Fleet {
            val weightedClasses = WeightedList(
                    ShipClass.SMALL_PASSENGER_CARRIER to 52,
                    ShipClass.MEDIUM_PASSENGER_CARRIER to 44,
                    ShipClass.LARGE_PASSENGER_CARRIER to 34,
                    ShipClass.HUGE_PASSENGER_CARRIER to 24,
                    ShipClass.CRUISE_LINER to 16,
                    ShipClass.DREAM_LINER to 12,
                    ShipClass.SMALL_COLONY_SHIP to 12,
                    ShipClass.LARGE_COLONY_SHIP to 8,
                    ShipClass.LIVESHIP to 4,
                    ShipClass.CITYSHIP to 3,
                    ShipClass.SMALL_FREIGHT_CARRIER to 28,
                    ShipClass.MEDIUM_FREIGHT_CARRIER to 22,
                    ShipClass.LARGE_FREIGHT_CARRIER to 14,
                    ShipClass.HUGE_FREIGHT_CARRIER to 8,
                    ShipClass.SUPER_FREIGHT_CARRIER to 4,
                    ShipClass.REFINERY_SHIP to 6,
                    ShipClass.FUEL_TANKER to 3,
                    ShipClass.MOBILE_DRY_DOCK to 1,
                    ShipClass.MINING_SHIP to 6,
                    ShipClass.CORVETTE to 20,
                    ShipClass.SCOUT to 14,
                    ShipClass.DESTROYER to 16,
                    ShipClass.TROOP_CARRIER to 12,
                    ShipClass.FRIGATE to 8,
                    ShipClass.CRUISER to 6,
                    ShipClass.HEAVY_CRUISER to 6,
                    ShipClass.CARRIER to 4,
                    ShipClass.BATTLESHIP to 4,
                    ShipClass.DREADNOUGHT to 4,
                    ShipClass.FLEET_CARRIER to 2,
                    ShipClass.TITAN to 2,
                    ShipClass.BATTLECARRIER to 1
            )

            val ships = Random.sample(shipNames, numShips).mapTo(mutableListOf()) { name ->
                val cls = Random.choice(weightedClasses)
                Ship(name, cls)
            }

            return Fleet(ships, startingLocation, startingLocation)
        }
    }
}
