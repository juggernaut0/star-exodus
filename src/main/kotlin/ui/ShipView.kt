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
    val cargo get() = "${ship.inventory.usedSpace}/${ship.shipClass.cargoCapacity}"
    val inventory get() = ship.inventory.items.map { (ii, c) -> InventoryContents(ii, c) }

    val foodProd get() = ship.shipClass.foodProduction
    val foodCons get() = ship.foodConsumption

    fun lowFood(days: Int) = ship.inventory[InventoryItem.FOOD] < ship.foodConsumption * days
    fun lowFuel(days: Int, fleet: Fleet) = ship.inventory[InventoryItem.FUEL] < fleet.fuelConsumptionAtSpeed(ship) * days

    val explorers get() = ship.explorers
    val exploring get() = ship.exploring?.name?.let { "$it ($explorers explorers)" } ?: "None"

    val mining get() = ship.mining?.run { "${resource.name.toTitleCase()} on ${planet.name}" } ?: "None"

    @JsName("miningYield")
    fun miningYield(planet: PlanetView?, resource: InventoryItem?): String {
        if(planet == null || resource == null) return "..."
        return ship.miningYield(Ship.MiningTarget(planet.planet, resource)).toString()
    }

    class InventoryContents(val item: InventoryItem, val count: Int, var selected: Int = 0) {
        val itemName = item.name.toTitleCase()
        var validClass = ""
    }

    override fun toString(): String {
        return nameclass
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is ShipView && ship == other.ship
    }

    override fun hashCode(): Int {
        return ship.hashCode() * 31
    }
}
