package ui

import game.Inventory
import game.Ship
import game.StarSequence
import game.StarSystem

// Bootstrap button
fun bsBtn(type: String) = listOf("btn", "btn-$type")
fun bsBtnBlock(type: String) = listOf("btn", "btn-$type", "btn-block")
fun bsBtnOutline(type: String) = listOf("btn", "btn-outline-$type")

// Bootstrap toggles
fun bsModalToggle(target: String) = mapOf("data-toggle" to "modal", "data-target" to "#$target")
fun bsCollapseToggle(target: String, parent: String): Map<String, String> {
    return mapOf("data-toggle" to "collapse", "data-target" to "#$target", "data-parent" to "#$parent")
}

const val CLOSE = "\u00d7"

fun Ship.toView(): ShipView = ShipView(this)
fun StarSystem.toView() = StarView(this)
fun StarSequence.StarTarget.toView() = StarView(star, distance)

fun Inventory.toView() = items.map { (ii, c) -> InventoryContents(ii, c) }