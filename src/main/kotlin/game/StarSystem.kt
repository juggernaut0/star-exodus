package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.Random
import kotlin.math.roundToInt

class StarSystem(
        val name: String,
        val type: StarType,
        val planets: List<Planet>,
        threat: Double,
        threatRate: Double
) {
    val passiveRepair get() = planets.asSequence().flatMap { it.discoveredFeatures.asSequence() }.sumBy { it.passiveRepair }

    var threat = threat
        internal set
    var threatRate = threatRate
        internal set

    internal fun increaseThreat() {
        threat += threatRate
    }

    companion object {
        operator fun invoke(forceName: String? = null): StarSystem {
            val name = forceName ?: Random.choice(ExodusGame.resources.getStarNames())
            val type = Random.choice(StarType.values())
            val numPlanets = Random.normal(mu = 4.0, sigma = 2.5).roundToInt().coerceAtLeast(1)
            val planets = List(numPlanets) { i ->
                val pName = "$name ${romanNumeral(i+1)}"
                val pType = Random.choice(type.planetTypes)
                Planet(pName, pType)
            }
            return StarSystem(name, type, planets, 0.0, 0.01)
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
                11 -> "XI"
                12 -> "XII"
                else -> n.toString()
            }
        }
    }

    object Serial : Serializer<StarSystem, Serial.Data> {
        @Serializable
        class Data(
                val name: String,
                val type: StarType,
                val planets: List<Int>,
                val threat: Double,
                val threatRate: Double)

        override fun save(model: StarSystem, refs: RefSaver): Serial.Data {
            return Data(
                    model.name,
                    model.type,
                    model.planets.map { refs.savePlanetRef(it) },
                    model.threat,
                    model.threatRate
            )
        }

        override fun load(data: Serial.Data, refs: RefLoader): StarSystem {
            return StarSystem(
                    data.name,
                    data.type,
                    data.planets.map { refs.loadPlanetRef(it) },
                    data.threat,
                    data.threatRate
            )
        }
    }
}
