package ui

import game.Planet
import game.PlanetFeature
import util.toTitleCase

class PlanetView(internal val planet: Planet) {
    val name: String = planet.name
    val type: String = planet.type.name.toTitleCase()
    val nametype: String = "$name - $type"

    val exploration: String = "${planet.exploration}%"
    val features: Array<FeatureView> = let {
        val feats = planet.discoveredFeatures
        List(5) { i -> FeatureView(if (i < feats.size) feats[i] else null) }.toTypedArray()
    }

    val tradable get() = planet.tradable

    val inventory = planet.inventory.items.map { (ii, c) -> ShipView.InventoryContents(ii, c) }.toTypedArray()

    class FeatureView(feature: PlanetFeature?) {
        val name = feature?.name?.toTitleCase() ?: "???"
        val description = feature?.description
    }
}