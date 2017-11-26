package game

import serialization.Serializable
import util.IntVector2
import util.Random

class Galaxy(val stars: List<StarSystem>, val mapSize: Int) : Serializable {

    fun getNearbyStars(location: IntVector2, radius: Double) =
            stars.filter { IntVector2.distance(it.location, location) <= radius }

    fun getStarAt(location: IntVector2) = getNearbyStars(location, 0.0).firstOrNull()

    companion object {
        operator fun invoke(numStars: Int, mapSize: Int, starNames: List<String>): Galaxy {
            val uniqueNames = Random.sample(starNames, numStars)
            val stars = uniqueNames.map {
                val loc = IntVector2(Random.range(mapSize), Random.range(mapSize))
                StarSystem(it, loc)
            }
            return Galaxy(stars, mapSize)
        }
    }
}
