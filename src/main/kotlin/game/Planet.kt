package game

import util.Random
import util.shuffle
import kotlin.js.Math

class Planet(val name: String, val type: PlanetType) {
    var exploration: Int = 0 // out of 100
        private set
    val features: List<PlanetFeature>

    init {
        features = ArrayList()
        while (features.size < 5) {
            val feat = Random.choice(type.features)
            if (feat == PlanetFeature.NOTHING || !features.contains(feat)) {
                features.add(feat)
            }
        }
        features.shuffle()
    }

    fun explore(numExplorers: Int, onDiscover: (PlanetFeature) -> Unit) {
        val begin = exploration
        exploration = Math.max(exploration + numExplorers / 5, 100)
        val end = exploration
        if(begin % 20 > end % 20 || end - begin >= 20){
            val n = begin / 20
            val l = end / 20
            for (i in n until l) {
                onDiscover(features[i])
            }
        }
    }
}
