package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.Event
import util.EventEmitter
import util.Random

class ExodusGame(val fleet: Fleet, day: Int) : EventEmitter<ExodusGame>() {
    val onTurn = Event<ExodusGame, Unit>().bind(this)

    var day = day
        private set

    fun nextDay(){
        day += 1

        fleet.doTurn()
        onTurn(Unit)
    }

    companion object {
        lateinit var resources: ResourceLoader

        operator fun invoke(): ExodusGame {
            val fleet = Fleet(10)
            return ExodusGame(fleet, 0)
        }
    }

    object Serial : Serializer<ExodusGame, Serial.Data> {
        @Serializable
        class Data(val fleet: Fleet.Serial.Data, val day: Int)

        override fun save(model: ExodusGame, refs: RefSaver): Data {
            return Data(Fleet.Serial.save(model.fleet, refs), model.day)
        }

        override fun load(data: Data, refs: RefLoader): ExodusGame {
            return ExodusGame(Fleet.Serial.load(data.fleet, refs), data.day)
        }
    }
}
