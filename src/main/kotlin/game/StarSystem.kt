package game

import util.Location
import util.Random

class StarSystem(val name: String, val location: Location) {
    val type: StarType = Random.choice(StarType.values())
    val planets: List<Planet>

    init {
        val numPlanets = Random.normal(mu = 4.0, sigma = 2.5).toInt()
        planets = List(numPlanets) { i ->
            val pName = "$name ${romanNumeral(i+1)}"
            val pType = Random.choice(type.planetTypes)
            Planet(pName, pType)
        }
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
