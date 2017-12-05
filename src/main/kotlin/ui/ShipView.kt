package ui

import game.Fleet
import game.InventoryItem
import game.Ship
import util.toTitleCase

class ShipView(internal val ship: Ship) {
    val name get() = ship.name
    val shipClass = ship.shipClass.displayName
    val nameclass get() = "${ship.name} - ${ship.shipClass.displayName}"
    val hull get() = "${ship.hullPoints}/${ship.maxHull}"
    val crew get() = "${ship.crew}/${ship.maxCrew}"
    val cargo = "${ship.inventory.usedSpace}/${ship.shipClass.cargoCapacity}"
    val inventory = ship.inventory.items.map { (ii, c) -> InventoryContents(ii.name.toTitleCase(), c) }.toTypedArray()

    val foodProd = ship.shipClass.foodProduction
    val foodCons get() = ship.foodConsumption

    fun lowFood(days: Int) = ship.inventory[InventoryItem.FOOD] < ship.foodConsumption * days
    fun lowFuel(days: Int, fleet: Fleet) = ship.inventory[InventoryItem.FUEL] < fleet.fuelConsumptionAtSpeed(ship) * days

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
