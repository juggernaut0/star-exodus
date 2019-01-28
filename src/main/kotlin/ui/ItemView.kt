package ui

import game.InventoryItem
import util.toTitleCase

class ItemView(val item: InventoryItem) {
    override fun toString(): String {
        return item.name.toTitleCase()
    }

    override fun equals(other: Any?): Boolean {
        return other is ItemView && item == other.item
    }

    override fun hashCode(): Int {
        return 31 * item.hashCode()
    }
}
