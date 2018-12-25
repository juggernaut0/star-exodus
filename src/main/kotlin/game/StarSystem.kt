package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.IntVector2
import util.Random

class StarSystem(val name: String, val location: IntVector2, val type: StarType, val planets: List<Planet>) {
    companion object {
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

    object Serial : Serializer<StarSystem, Serial.Data> {
        @Serializable
        class Data(val name: String, val location: IntVector2, val type: StarType, val planets: List<Int>)

        override fun save(model: StarSystem, refs: RefSaver): Serial.Data {
            return Data(model.name, model.location, model.type, model.planets.map { refs.savePlanetRef(it) })
        }

        override fun load(data: Serial.Data, refs: RefLoader): StarSystem {
            return StarSystem(data.name, data.location, data.type, data.planets.map { refs.loadPlanetRef(it) })
        }
    }
}
