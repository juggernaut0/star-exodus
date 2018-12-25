package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.Event
import util.EventEmitter
import util.Random

class ExodusGame(val galaxy: Galaxy, val fleet: Fleet, day: Int) : EventEmitter<ExodusGame>() {
    val onTurn = Event<ExodusGame, Unit>().bind(this)

    var day = day
        private set

    fun nextDay(){
        day += 1

        fleet.doTurn(this)
        onTurn(Unit)
    }

    companion object {
        operator fun invoke(resourceLoader: ResourceLoader): ExodusGame {
            val galaxy = Galaxy(400, 10000, resourceLoader.getStarNames())
            val startingLoc = Random.choice(galaxy.stars).location
            val fleet = Fleet(10, resourceLoader.getShipNames(), startingLoc, galaxy.getNearbyStars(startingLoc, Fleet.SENSOR_RANGE))
            return ExodusGame(galaxy, fleet, 0)
        }
    }

    object Serial : Serializer<ExodusGame, Serial.Data> {
        @Serializable
        class Data(val galaxy: Galaxy.Serial.Data, val fleet: Fleet.Serial.Data, val day: Int)

        override fun save(model: ExodusGame, refs: RefSaver): Data {
            return Data(Galaxy.Serial.save(model.galaxy, refs), Fleet.Serial.save(model.fleet, refs), model.day)
        }

        override fun load(data: Data, refs: RefLoader): ExodusGame {
            return ExodusGame(Galaxy.Serial.load(data.galaxy, refs), Fleet.Serial.load(data.fleet, refs), data.day)
        }
    }
}
