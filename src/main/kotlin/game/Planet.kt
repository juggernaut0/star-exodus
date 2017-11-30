package game

import serialization.Serializable
import util.*
import kotlin.math.min

class Planet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, exploration: Int) : Serializable, EventEmitter<Planet>() {
    val onDiscoverFeature = Event<Planet, PlanetFeature>().bind(this)

    var exploration: Int = exploration // out of 100
        private set

    val discoveredFeatures get() = features.subList(0, (exploration/20))

    val oreAmount: Int
        get() {
            val feats = discoveredFeatures
            return when {
                PlanetFeature.POOR_RESOURCE_DEPOSITS in feats -> 8
                PlanetFeature.RICH_RESOURCE_DEPOSITS in feats -> 16
                else -> 0
            }
        }

    fun explore(ships: List<Ship>) {
        if (ships.isEmpty()) return

        val numExplorers = ships.sumBy { it.explorers }
        val begin = exploration
        exploration = min(exploration + numExplorers / 5, 100)
        val end = exploration
        if(begin % 20 > end % 20 || end - begin >= 20){
            val n = begin / 20
            val l = end / 20
            (n until l)
                    .asSequence()
                    .filter { features[it] != PlanetFeature.NOTHING }
                    .forEach { onDiscoverFeature(features[it]) }
        }

        if (exploration == 100) {
            ships.forEach { it.exploring = null }
        }
    }

    companion object {
        operator fun invoke(name: String, type: PlanetType): Planet {
            val features = mutableListOf<PlanetFeature>()
            while (features.size < 5) {
                val feat = Random.choice(type.features)
                if (feat == PlanetFeature.NOTHING || feat !in features) {
                    features.add(feat)
                }
            }
            features.shuffle()
            return Planet(name, type, features, 0)
        }
    }
}
