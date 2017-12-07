package ui

import game.Planet
import util.toTitleCase

class PlanetView(internal val planet: Planet) {
    val name: String = planet.name
    val type: String = planet.type.name.toTitleCase()
    val nametype: String = "$name - $type"

    val exploration: String = "${planet.exploration}%"
    val features: Array<String> = let {
        val feats = planet.discoveredFeatures
        List(5) { i -> if (i < feats.size) feats[i].name.toTitleCase() else "???" }.toTypedArray()
    }

    val tradable = planet.inventory.capacity > 0

    val inventory = planet.inventory.items.map { (ii, c) -> ShipView.InventoryContents(ii.name.toTitleCase(), c) }.toTypedArray()
}