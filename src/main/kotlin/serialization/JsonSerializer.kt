package serialization

import game.*
import kotlinx.serialization.json.JSON

object JsonSerializer {
    fun save(model: ExodusGame): String {
        val refs = RefSaver()
        refs.objects.game = ExodusGame.Serial.save(model, refs)
        return JSON.stringify(ObjectContainer.serializer(), refs.objects)
    }

    fun load(data: String): ExodusGame {
        val objs = JSON.parse(ObjectContainer.serializer(), data)
        val refs = RefLoader(objs)
        return ExodusGame.Serial.load(objs.game!!, refs)
    }
}
