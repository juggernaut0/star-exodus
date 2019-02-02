package ui

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
    val inventory get() = ship.inventory.toView()

    val foodProd get() = ship.shipClass.foodProduction
    val foodCons get() = ship.foodConsumption

    fun lowFood(days: Int) = ship.inventory[InventoryItem.FOOD] < ship.foodConsumption * days
    fun lowFuel(distance: Int) = ship.inventory[InventoryItem.FUEL] < ship.fuelConsumption(distance)

    val explorers get() = ship.explorers
    val exploring get() = ship.exploring?.name?.let { "$it ($explorers explorers)" } ?: "None"

    val mining get() = ship.mining?.let { "${it.resource.name.toTitleCase()} (${ship.miningYield(it)}) on ${it.planet.name}" } ?: "None"

    @JsName("miningYield")
    fun miningYield(planet: PlanetView?, resource: InventoryItem?): String {
        if(planet == null || resource == null) return "..."
        return ship.miningYield(Ship.MiningTarget(planet.planet, resource)).toString()
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
