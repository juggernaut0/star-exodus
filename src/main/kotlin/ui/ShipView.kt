package ui

import game.Fleet
import game.InventoryItem
import game.Ship
import util.toTitleCase

class ShipView(internal val ship: Ship) {
    val name get() = ship.name
    val shipClass = ship.shipClass.displayName
    val hull = "${ship.hullPoints}/${ship.shipClass.maxHull}"
    val crew = "${ship.crew}/${ship.shipClass.maxCrew}"
    val cargo = "${ship.inventory.usedSpace}/${ship.shipClass.cargoCapacity}"
    val inventory = ship.inventory.items.map { (ii, c) -> InventoryContents(ii.name.toTitleCase(), c) }.toTypedArray()

    val foodProd = ship.shipClass.foodProduction
    val foodCons = ship.foodConsumption

    fun lowFood() = ship.inventory[InventoryItem.FOOD] < ship.foodConsumption * 3
    fun lowFuel(fleet: Fleet) = ship.inventory[InventoryItem.FUEL] < fleet.fuelConsumptionAtSpeed(ship) * 3

    val explorers = ship.explorers
    val exploring get() = ship.exploring?.name?.let { "$it ($explorers explorers)" } ?: "None"

    val mining get() = ship.mining?.run { "${resource.name.toTitleCase()} on ${planet.name}" } ?: "None"

    @JsName("miningYield")
    fun miningYield(planet: PlanetView?, resourceName: String?): String {
        if(planet == null || resourceName == null) return "..."
        return ship.miningYield(Ship.MiningTarget(planet.planet, InventoryItem.valueOf(resourceName))).toString()
    }

    class InventoryContents(val itemName: String, val count: Int)
}
