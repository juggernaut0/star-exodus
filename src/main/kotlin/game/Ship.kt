package game

class Ship(val name: String, val shipClass: ShipClass) {
    var hullPoints = shipClass.maxHull
        private set
    var crew = (shipClass.maxCrew * 0.9).toInt()
        private set
    val inventory: Inventory = Inventory(shipClass.cargoCapacity)

    val mass get() = shipClass.maxCrew/2 + inventory.freeSpace/2 + inventory.usedSpace + shipClass.hanger*2
    val destroyed get() = hullPoints == 0

    fun modHullPoints(amt: Int): Int {
        hullPoints = minOf(maxOf(hullPoints + amt, 0), shipClass.maxHull)
        return hullPoints
    }
}