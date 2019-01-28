package ui

import game.Planet
import game.PlanetFeature
import util.toTitleCase

class PlanetView(internal val planet: Planet) {
    val name: String = planet.name
    val type: String = planet.type.name.toTitleCase()
    val nametype: String = "$name - $type"

    val exploration get() = "${planet.exploration}%"
    val features: List<FeatureView> get() {
        val feats = planet.discoveredFeatures
        return List(5) { i -> FeatureView(if (i < feats.size) feats[i] else null) }
    }
    val isExplored get() = planet.exploration == 100

    val tradable get() = planet.tradable

    val inventory get() = planet.inventory.toView()

    class FeatureView(feature: PlanetFeature?) {
        val name = feature?.name?.toTitleCase() ?: "???"
        val description = feature?.description
    }

    override fun toString(): String {
        return nametype
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && other is PlanetView && planet == other.planet
    }

    override fun hashCode(): Int {
        return planet.hashCode() * 31
    }


}