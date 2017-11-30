package game

import serialization.Serializable
import util.Event
import util.EventEmitter
import util.Random

class ExodusGame(val galaxy: Galaxy, val fleet: Fleet, day: Int) : Serializable, EventEmitter<ExodusGame>() {
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
            val fleet = Fleet(10, resourceLoader.getShipNames(), Random.choice(galaxy.stars).location)
            return ExodusGame(galaxy, fleet, 0)
        }
    }
}
