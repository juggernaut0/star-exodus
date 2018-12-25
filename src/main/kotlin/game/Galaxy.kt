package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.IntVector2
import util.Random

class Galaxy(val stars: List<StarSystem>, val mapSize: Int) {

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

    object Serial : Serializer<Galaxy, Serial.Data> {
        @Serializable
        class Data(val stars: List<Int>, val mapSize: Int)

        override fun save(model: Galaxy, refs: RefSaver): Data {
            return Data(model.stars.map { refs.saveStarRef(it) }, model.mapSize)
        }

        override fun load(data: Data, refs: RefLoader): Galaxy {
            return Galaxy(data.stars.map { refs.loadStarRef(it) }, data.mapSize)
        }
    }
}
