package serialization

import util.Location

object JsonSerializer {
    private fun array(vals: List<String>): String {
        return vals.joinToString(separator = ",", prefix = "[", postfix = "]")
    }

    private fun jsonify(value: Any?): String = when (value) {
        is Number, is Boolean -> value.toString()
        is String -> "\"$value\""
        is Enum<*> -> "\"${value.name}\""
        is Location -> "{\"x\":${value.x},\"y\":${value.y}}"
        is List<*> -> array(value.map { jsonify(it) })
        is Map<*, *> -> "null" // TODO
        is SerializationModel -> value.toJson()
        else -> "null"
    }

    fun SerializationModel.toJson(): String {
        return props.joinToString(",", "{", "}") { (key, value) -> "\"$key\":${jsonify(value)}" }
    }

    private fun verifySGame(obj: dynamic): SGame {
        return SGame(verifySGalaxy(obj.galaxy), verifySFleet(obj.fleet), verifyInt(obj.day))
    }

    private fun verifySGalaxy(obj: dynamic): SGalaxy {
        if (obj == undefined) throw DeserializationException("galaxy is undefined")
        TODO()
    }

    private fun verifySFleet(obj: dynamic): SFleet {

    }

    private fun verifyInt(obj: dynamic): Int {

    }

    fun load(json: String): SGame = verifySGame(JSON.parse(json))
}
