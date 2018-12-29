package ui

import game.InventoryItem
import util.toTitleCase

class InventoryContents(val item: InventoryItem, val count: Int, var selected: Int = 0) {
    val itemName = item.name.toTitleCase()
    var validClass = ""
}
