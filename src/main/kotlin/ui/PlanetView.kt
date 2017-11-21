package ui

import game.Planet
import util.toTitleCase

class PlanetView(planet: Planet) {
    val name: String = planet.name
    val type: String = planet.type.name.toTitleCase()
}