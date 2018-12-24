package ui

import game.Ship

// Bootstrap button
fun bsBtnBlock(type: String) = listOf("btn", "btn-$type", "btn-block")
fun bsBtnOutline(type: String) = listOf("btn", "btn-outline-$type")

// Bootstrap toggles
fun bsModalToggle(target: String) = mapOf("data-toggle" to "modal", "data-target" to "#$target")
fun bsCollapseToggle(target: String, parent: String): Map<String, String> {
    return mapOf("data-toggle" to "collapse", "data-target" to "#$target", "data-parent" to "#$parent")
}

const val CLOSE = "\u00d7"

fun Ship.toView(): ShipView = ShipView(this)
