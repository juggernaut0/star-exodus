package ui

import game.ExodusGame
import game.Planet
import game.StarSystem
import util.Deque
import util.event
import util.EventEmitter
import util.toTitleCase

class EventLog(gameService: GameService) : EventEmitter<EventLog>() {
    private val _messages = Deque<String>()
    val messages: Collection<String> get() = _messages

    val eventAdded = event<EventLog, String>()

    init {
        _messages.pushFront("Welcome to Star Exodus!")

        gameService.onReady += { sender, _ ->
            registerGameListeners(sender.game)
        }
    }

    private fun registerGameListeners(game: ExodusGame) {
        game.onTurn += { sender, _ -> log("Day ${sender.day}") }

        game.fleet.onArrive += { _, star ->
            log("The fleet has arrived in the ${star.name} system.")
            registerPlanetListeners(star)
        }

        registerPlanetListeners(game.fleet.currentLocation)

        for (ship in game.fleet.ships) {
            ship.onMine += { sender, args -> log("${sender.name} has gathered ${args.amount} ${args.resource.name.toTitleCase()} from ${args.planet.name}.") }
            ship.onRepair += { sender, amt -> log("${sender.name} has repaired for $amt hull points.") }
            ship.onBirth += { sender, amt ->
                val pl = if (amt == 1) " has" else "s have"
                log("$amt birth$pl occured on ${sender.name}")
            }
            ship.onDeath += { sender, amt ->
                val pl = if (amt == 1) "" else "s"
                log("$amt death$pl occured on ${sender.name}")
            }
        }
    }

    private fun registerPlanetListeners(star: StarSystem) {
        for (planet in star.planets) {
            planet.onDiscoverFeature += { sender, args ->
                var msg = "${args.feature.name.toTitleCase()} has been discovered on ${sender.name}."
                if (args.result != null) {
                    args.result.resources
                            .asSequence()
                            .filter { (_, amt) -> amt > 0 }
                            .forEach { (item, amt) -> msg += "\n$amt ${item.name.toTitleCase()} was collected." }
                }
                log(msg)
            }
        }
    }

    private fun log(msg: String) {
        _messages.pushFront(msg)
        if (_messages.size > 20) {
            _messages.popBack()
        }
        eventAdded(msg)
    }
}