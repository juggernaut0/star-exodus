package game

import serialization.SerializationModels.SGalaxy
import serialization.Serializer
import util.Location
import util.Random

class Galaxy private constructor(val stars: List<StarSystem>, val mapSize: Int) {

    fun getNearbyStars(location: Location, radius: Double) =
            stars.filter { Location.distance(it.location, location) <= radius }

    fun getStarAt(location: Location) = getNearbyStars(location, 0.0).firstOrNull()

    companion object : Serializer<Galaxy, SGalaxy> {
        override fun serialize(obj: Galaxy): SGalaxy =
                SGalaxy(obj.stars.map { StarSystem.serialize(it) }, obj.mapSize)

        override fun deserialize(serModel: SGalaxy): Galaxy =
                Galaxy(serModel.stars.map { StarSystem.deserialize(it) }, serModel.mapSize)

        operator fun invoke(numStars: Int, mapSize: Int, starNames: List<String>): Galaxy {
            val uniqueNames = Random.sample(starNames, numStars)
            val stars = uniqueNames.map {
                val loc = Location(Random.range(mapSize), Random.range(mapSize))
                StarSystem(it, loc)
            }
            return Galaxy(stars, mapSize)
        }
    }
}
