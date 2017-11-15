package ui

import game.StarSystem

class StarView(star: StarSystem) {
    val name: String = star.name
    val type: String = star.type.displayName
    val planets: Array<PlanetView> = star.planets.map { PlanetView(it) }.toTypedArray()
}