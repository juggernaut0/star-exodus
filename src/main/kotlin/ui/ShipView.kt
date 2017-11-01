package ui

import game.Ship

class ShipView(private val ship: Ship) {
    val name get() = ship.name
    val shipClass get() = ship.shipClass.name
    val hull get() = "${ship.hullPoints}/${ship.shipClass.maxHull}"
    val crew get() = "${ship.crew}/${ship.shipClass.maxCrew}"
    val cargo get() = "${ship.inventory.usedSpace}/${ship.shipClass.cargoCapacity}"
}