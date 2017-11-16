package ui

import game.StarSystem
import util.IntVector2

class StarView(star: StarSystem) {
    val name: String = star.name
    val type: String = star.type.displayName
    val planets: Array<PlanetView> = star.planets.map { PlanetView(it) }.toTypedArray()
    val location: IntVector2 = star.location
}