package game

import util.Location
import util.Random

class Galaxy(numStars: Int, mapSize: Int, starNames: List<String>) {
    val stars: List<StarSystem>

    init {
        val uniqueNames = Random.sample(starNames, numStars)
        stars = uniqueNames.map {
            val loc = Location(Random.range(mapSize), Random.range(mapSize))
            StarSystem(it, loc)
        }
    }

    fun getNearbyStars(location: Location, radius: Double) =
            stars.filter { Location.distance(it.location, location) <= radius }
}
