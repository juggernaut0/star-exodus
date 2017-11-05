package serialization

import util.Location

object JsonSerializer {
    fun SGame.toJson() = obj(
            "galaxy" to galaxy.toJson()
    )

    fun SGalaxy.toJson() = obj(
            "mapSize" to mapSize.toJson(),
            "stars" to array(stars.map { it.toJson() })
    )

    fun SStarSystem.toJson() = obj(
            "name" to name.toJson(),
            "type" to type.toJson(),
            "location" to location.toJson(),
            "planets" to array(planets.map { it.toJson() })
    )

    fun SPlanet.toJson(): String = obj(
            "name" to name.toJson(),
            "type" to type.toJson(),
            "exploration" to exploration.toJson(),
            "features" to array(features.map { it.toJson() })
    )

    fun Location.toJson() = obj("x" to x.toJson(), "y" to y.toJson())

    private fun <E : Enum<E>> Enum<E>.toJson() = "\"${this::class.simpleName}.$name\""
    private fun String.toJson() = "\"$this\""
    private fun Number.toJson() = toString()

    private fun obj(vararg props: Pair<String, String>): String {
        return props.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, value) -> "\"$key\":$value" }
    }

    private fun array(vararg vals: String): String {
        return vals.joinToString(separator = ",", prefix = "[", postfix = "]")
    }
    private fun array(vals: List<String>): String {
        return vals.joinToString(separator = ",", prefix = "[", postfix = "]")
    }
}
