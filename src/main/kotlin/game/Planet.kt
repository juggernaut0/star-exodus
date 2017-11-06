package game

import serialization.SerializationModels.SPlanet
import serialization.Serializer
import util.Random
import util.shuffle
import kotlin.js.Math

class Planet private constructor(val name: String, val type: PlanetType, val features: List<PlanetFeature>, exploration: Int) {

    var exploration: Int = exploration // out of 100
        private set

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

    companion object : Serializer<Planet, SPlanet> {
        override fun serialize(obj: Planet): SPlanet = SPlanet(obj.name, obj.type, obj.features, obj.exploration)

        override fun deserialize(serModel: SPlanet): Planet =
                Planet(serModel.name, serModel.type, serModel.features, serModel.exploration)

        operator fun invoke(name: String, type: PlanetType): Planet {
            val features = mutableListOf<PlanetFeature>()
            while (features.size < 5) {
                val feat = Random.choice(type.features)
                if (feat == PlanetFeature.NOTHING || !features.contains(feat)) {
                    features.add(feat)
                }
            }
            features.shuffle()
            return Planet(name, type, features, 0)
        }
    }
}
