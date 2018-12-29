package ui

import game.StarSystem
import util.toTitleCase

class StarView(star: StarSystem, val distance: Int? = null) {
    val name: String = star.name
    val type: String = star.type.name.toTitleCase()
    val planets: List<PlanetView> = star.planets.map { PlanetView(it) }

    override fun toString(): String {
        return "$name - $type"
    }
}