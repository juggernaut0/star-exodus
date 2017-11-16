package game

import serialization.SerializationModels.SGalaxy
import serialization.Serializer
import util.IntVector2
import util.Random

class Galaxy private constructor(val stars: List<StarSystem>, val mapSize: Int) {

    fun getNearbyStars(location: IntVector2, radius: Double) =
            stars.filter { IntVector2.distance(it.location, location) <= radius }

    fun getStarAt(location: IntVector2) = getNearbyStars(location, 0.0).firstOrNull()

    companion object : Serializer<Galaxy, SGalaxy> {
        override fun serialize(obj: Galaxy): SGalaxy =
                SGalaxy(obj.stars.map { StarSystem.serialize(it) }, obj.mapSize)

        override fun deserialize(serModel: SGalaxy): Galaxy =
                Galaxy(serModel.stars.map { StarSystem.deserialize(it) }, serModel.mapSize)

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
