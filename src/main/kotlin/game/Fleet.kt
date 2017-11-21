package game

import serialization.SerializationModels.SFleet
import serialization.Serializer
import util.IntVector2
import util.Random
import util.WeightedList
import kotlin.js.Math

class Fleet private constructor(private val _ships: MutableCollection<Ship>, location: IntVector2, var destination: IntVector2) {
    var location: IntVector2 = location
        private set

    val ships: Collection<Ship> get() = _ships

    val speed: Int get() = ships.asSequence().map { it.shipClass.speed }.min() ?: 0

    internal fun moveTowardsDestination() {
        if (destination == location) return

        val dist = IntVector2.distance(destination, location)
        if (dist <= speed) {
            location = destination
        } else {
            location += (destination - location) * (speed / dist)
        }
        val fuelNeeded = _ships.associate { it to Math.ceil(it.fuelConsumption * Math.min(speed.toDouble(), dist)) }
        _ships.removeAll { it.inventory[InventoryItem.FUEL] < fuelNeeded[it]!! }
        _ships.forEach { it.inventory.removeItems(InventoryItem.FUEL, fuelNeeded[it]!!) }

        // TODO discovered systems
    }

    internal fun abandonUncrewed() {
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

    companion object : Serializer<Fleet, SFleet> {
        override fun serialize(obj: Fleet): SFleet =
                SFleet(obj.ships.map { Ship.serialize(it) }, obj.location, obj.destination)

        override fun deserialize(serModel: SFleet): Fleet =
                Fleet(serModel.ships.map { Ship.deserialize(it) }.toMutableList(), serModel.location, serModel.destination)

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
