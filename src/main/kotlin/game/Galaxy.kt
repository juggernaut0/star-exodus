package game

import util.Location
import util.Random

class Galaxy(numStars: Int, mapSize: Int) {
    val stars: List<StarSystem> = List(numStars) {
        val loc = Location(Random.range(mapSize), Random.range(mapSize))
        val name = "" // TODO
        StarSystem(name, loc)
    }

    fun getNearbyStars(location: Location, radius: Double) =
            stars.filter { Location.distance(it.location, location) <= radius }
}
