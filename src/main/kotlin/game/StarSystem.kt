package game

import serialization.SerializationModels.SStarSystem
import serialization.Serializer
import util.IntVector2
import util.Random

class StarSystem private constructor(val name: String, val location: IntVector2, val type: StarType, val planets: List<Planet>) {
    companion object : Serializer<StarSystem, SStarSystem> {
        override fun serialize(obj: StarSystem): SStarSystem =
                SStarSystem(obj.name, obj.location, obj.type, obj.planets.map { Planet.serialize(it) })

        override fun deserialize(serModel: SStarSystem): StarSystem =
                StarSystem(serModel.name, serModel.location, serModel.type, serModel.planets.map { Planet.deserialize(it) })

        operator fun invoke(name: String, location: IntVector2): StarSystem {
            val type = Random.choice(StarType.values())
            val numPlanets = Random.normal(mu = 4.0, sigma = 2.5).toInt().takeIf { it >= 0 } ?: 0
            val planets = List(numPlanets) { i ->
                val pName = "$name ${romanNumeral(i+1)}"
                val pType = Random.choice(type.planetTypes)
                Planet(pName, pType)
            }
            return StarSystem(name, location, type, planets)
        }

        private fun romanNumeral(n: Int): String {
            return when (n) {
                1 -> "I"
                2 -> "II"
                3 -> "III"
                4 -> "IV"
                5 -> "V"
                6 -> "VI"
                7 -> "VII"
                8 -> "VIII"
                9 -> "IX"
                10 -> "X"
                else -> n.toString()
            }
        }
    }
}
