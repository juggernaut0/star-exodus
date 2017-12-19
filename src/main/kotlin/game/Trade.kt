package game

import kotlin.math.max

class Trade(val shipInventory: Inventory, val planet: Planet) {
    // positive = planet -> ship
    val proposed: MutableMap<InventoryItem, Int> = mutableMapOf()

    val balance get() = proposed.entries.sumBy { (item, amt) -> amt * item.value }

    fun execute(): Boolean {
        if (!planet.tradable) return false

        val planetInventory = planet.inventory
        // make sure givers have enough
        for ((item, amt) in proposed.entries) {
            if (amt > planetInventory[item] || -amt > shipInventory[item]) return false
        }
        // make sure receivers have enough free space
        if (proposed.entries.sumBy { (_, a) -> max(a, 0) } > shipInventory.freeSpace) return false
        if (proposed.entries.sumBy { (_, a) -> max(-a, 0) } > planetInventory.freeSpace) return false
        // make sure balance of execute is reasonable
        if (balance > MAX_BALANCE) return false

        // do the execute
        for ((item, amt) in proposed.entries) {
            if (amt > 0) {
                planetInventory.transferItemsTo(shipInventory, item, amt)
            } else if (amt < 0) {
                shipInventory.transferItemsTo(planetInventory, item, -amt)
            }
        }

        return true
    }

    companion object {
        const val MAX_BALANCE = 10
    }
}