package game

import util.Random

class Ship(val name: String, val shipClass: ShipClass) {
    var hullPoints: Int = (shipClass.maxHull * Random.range(0.5, 1.0)).toInt()
        private set
    var crew: Int = (shipClass.maxCrew * Random.range(0.6, 0.9)).toInt()
        private set
    val inventory = Inventory(shipClass.cargoCapacity)

    val mass get() = shipClass.maxCrew/2 + inventory.freeSpace/2 + inventory.usedSpace + shipClass.hanger*2
    val destroyed get() = hullPoints == 0

    fun modHullPoints(amt: Int): Int {
        hullPoints = minOf(maxOf(hullPoints + amt, 0), shipClass.maxHull)
        return hullPoints
    }
}