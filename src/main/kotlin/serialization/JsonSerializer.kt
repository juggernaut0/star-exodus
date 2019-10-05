package serialization

import game.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object JsonSerializer {
    private val json = Json(JsonConfiguration.Stable)

    fun save(model: ExodusGame): String {
        val refs = RefSaver()
        refs.objects.game = ExodusGame.Serial.save(model, refs)
        return json.stringify(ObjectContainer.serializer(), refs.objects)
    }

    fun load(data: String): ExodusGame {
        val objs = json.parse(ObjectContainer.serializer(), data)
        val refs = RefLoader(objs)
        return ExodusGame.Serial.load(objs.game!!, refs)
    }
}
