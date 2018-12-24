package ui

import game.InventoryItem
import util.toTitleCase

class ItemView(val item: InventoryItem) {
    override fun toString(): String {
        return item.name.toTitleCase()
    }
}