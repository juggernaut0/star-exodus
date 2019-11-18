package game

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer

sealed class BlockedState {
    class Combat(val battle: Battle) : BlockedState()
    class Hailed : BlockedState()

    object Serial : Serializer<BlockedState, Serial.Data> {
        @Serializable
        class Data(
                val combat: Battle.Serial.Data? = null,
                val hailed: Unit? = null // TODO
        )

        override fun save(model: BlockedState, refs: RefSaver): Data {
            return when (model) {
                is Combat -> Data(combat = Battle.Serial.save(model.battle, refs))
                is Hailed -> Data(hailed = Unit)
            }
        }

        override fun load(data: Data, refs: RefLoader): BlockedState {
            return when {
                data.combat != null -> Combat(Battle.Serial.load(data.combat, refs))
                data.hailed != null -> Hailed()
                else -> throw SerializationException("Invalid state for BlockedState data: no fields were set")
            }
        }
    }
}
