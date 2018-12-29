package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer

class Galaxy(val main: MainSequence) {
    companion object {
        operator fun invoke(): Galaxy {
            return Galaxy(MainSequence())
        }
    }

    object Serial : Serializer<Galaxy, Serial.Data> {
        @Serializable
        class Data(val main: MainSequence.Serial.Data)

        override fun save(model: Galaxy, refs: RefSaver): Data {
            return Data(MainSequence.Serial.save(model.main, refs))
        }

        override fun load(data: Data, refs: RefLoader): Galaxy {
            return Galaxy(MainSequence.Serial.load(data.main, refs))
        }
    }
}
