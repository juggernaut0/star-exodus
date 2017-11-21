package ui

import game.StarSystem
import util.IntVector2
import util.toTitleCase

class StarView(star: StarSystem) {
    val name: String = star.name
    val type: String = star.type.name.toTitleCase()
    val planets: Array<PlanetView> = star.planets.map { PlanetView(it) }.toTypedArray()
    val location: IntVector2 = star.location
}