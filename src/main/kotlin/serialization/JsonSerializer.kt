package serialization

import serialization.SerializationModels.SGame
import util.Location
import kotlin.reflect.KClass

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

    private fun getProps(cls: KClass<out SerializationModel>): Array<Property<*>> {
        return SerializationModels.props[cls] ?: throw SerializationException("${cls.simpleName} does not have props")
    }

    private fun getCtor(cls: KClass<out SerializationModel>): (Array<Any>) -> SerializationModel {
        return SerializationModels.ctors[cls] ?: throw SerializationException("${cls.simpleName} does not have ctor")
    }

    fun SerializationModel.toJson(): String {
        val props = getProps(this::class)
        return props.joinToString(",", "{", "}") { "\"${it.name}\":${jsonify(it.getValue(this))}" }
    }

    private fun load(obj: dynamic, type: Type<*>): Any {
        @Suppress("UNCHECKED_CAST")
        return when (type.classifier) {
            in SerializationModels.ctors.keys -> load(obj, type.classifier as KClass<out SerializationModel>)
            Int::class -> (obj as Number).toInt()
            Double::class -> (obj as Number).toDouble()
            String::class -> obj as String
            List::class -> (obj as Array<dynamic>).map { load(it, type.arguments[0]) }
            Location::class -> Location((obj.x as Number).toInt(), (obj.y as Number).toInt())
            // TODO enums
            else -> throw SerializationException("Unknown type: ${type.classifier.simpleName} obj: $obj")
        }
    }

    private fun load(json: dynamic, cls: KClass<out SerializationModel>): SerializationModel {
        val ctor = getCtor(cls)
        val props = getProps(cls)
        val args = props.map { load(json[it.name], it.type) }.toTypedArray()
        return ctor(args)
    }

    fun loadGame(string: String): SGame = load(JSON.parse(string), SGame::class) as SGame
}
