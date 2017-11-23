package game

import serialization.SerializationModels.SGame
import serialization.Serializer
import util.Event
import util.EventEmitter
import util.Random

class ExodusGame private constructor(val galaxy: Galaxy, val fleet: Fleet, day: Int) : EventEmitter() {
    val onTurn = Event<ExodusGame, Unit>(this)

    var day = day
        private set

    fun nextDay(){
        day += 1

        fleet.doTurn()
        invoke(onTurn, Unit)
    }

    companion object : Serializer<ExodusGame, SGame> {
        override fun serialize(obj: ExodusGame): SGame =
                SGame(Galaxy.serialize(obj.galaxy), Fleet.serialize(obj.fleet), obj.day)

        override fun deserialize(serModel: SGame): ExodusGame =
                ExodusGame(Galaxy.deserialize(serModel.galaxy), Fleet.deserialize(serModel.fleet), serModel.day)

        operator fun invoke(resourceLoader: ResourceLoader): ExodusGame {
            val galaxy = Galaxy(400, 10000, resourceLoader.getStarNames())
            val fleet = Fleet(10, resourceLoader.getShipNames(), Random.choice(galaxy.stars).location)
            return ExodusGame(galaxy, fleet, 0)
        }
    }
}
