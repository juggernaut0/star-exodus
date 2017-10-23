package game

import util.Location
import util.Random

class StarSystem(val name: String, val location: Location) {
    val type: StarType = Random.choice(StarType.values())
}