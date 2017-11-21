package ui

import game.Ship
import util.toTitleCase

class ShipView(internal val ship: Ship) {
    val name get() = ship.name
    val shipClass = ship.shipClass.displayName
    val hull = "${ship.hullPoints}/${ship.shipClass.maxHull}"
    val crew = "${ship.crew}/${ship.shipClass.maxCrew}"
    val cargo = "${ship.inventory.usedSpace}/${ship.shipClass.cargoCapacity}"
    val inventory = ship.inventory.items.map { (ii, c) -> InventoryContents(ii.name.toTitleCase(), c) }.toTypedArray()

    class InventoryContents(val itemName: String, val count: Int)
}
