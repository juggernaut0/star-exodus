package game

import util.Random
import util.WeightedList

class Fleet(numShips: Int, shipClasses: Map<String, ShipClass>, shipNames: List<String>) {
    private val _ships: MutableCollection<Ship>
    val ships: Collection<Ship> get() = _ships

    val speed: Int get() = ships.map { it.shipClass.speed }.min() ?: 0

    init {
        val weightedClasses = WeightedList(
                "Small Passenger Carrier" to 52,
                "Medium Passenger Carrier" to 44,
                "Large Passenger Carrier" to 34,
                "Huge Passenger Carrier" to 24,
                "Cruise Liner" to 16,
                "Dream Liner" to 12,
                "Small Colony Ship" to 12,
                "Large Colony Ship" to 8,
                "Liveship" to 4,
                "Cityship" to 3,
                "Small Freight Carrier" to 28,
                "Medium Freight Carrier" to 22,
                "Large Freight Carrier" to 14,
                "Huge Freight Carrier" to 8,
                "Super Freight Carrier" to 4,
                "Refinery Ship" to 6,
                "Fuel Tanker" to 3,
                "Mobile Dry-Dock" to 1,
                "Mining Ship" to 6,
                "Corvette" to 20,
                "Scout" to 14,
                "Destroyer" to 16,
                "Troop Carrier" to 12,
                "Frigate" to 8,
                "Cruiser" to 6,
                "Heavy Cruiser" to 6,
                "Carrier" to 4,
                "Battleship" to 4,
                "Dreadnaught" to 4,
                "Fleet Carrier" to 2,
                "Titan" to 2,
                "BattleCarrier" to 1
        )

        _ships = Random.sample(shipNames, numShips).map { name ->
            Ship(name, shipClasses.getValue(Random.choice(weightedClasses)))
        }.toMutableList()
    }

    internal fun abandonUncrewed() {
        val uncrewed = _ships.filter { it.crew > it.shipClass.minCrew }
        val remaining = uncrewed.sumBy { it.crew }
        _ships.removeAll(uncrewed)
        // TODO distribute remaining
    }
}
