package ui

import game.ExodusGame
import util.Deque
import util.toTitleCase

@Suppress("MemberVisibilityCanPrivate", "unused")
class LogController(val gameService: GameService) {
    private val _log = Deque<String>()
    val log: Array<String> get() = _log.toTypedArray()

    init {
        _log.pushFront("Welcome to Star Exodus!")

        gameService.onReady += { sender, _ ->
            registerGameListeners(sender.game)
        }
    }

    private fun registerGameListeners(game: ExodusGame) {
        game.onTurn += { sender, _ ->
            log("Day ${sender.day}")
        }
        game.fleet.onArrive += { _, star -> log("The fleet has arrived in the ${star.name} system.") }
        game.galaxy.stars
                .flatMap { it.planets }
                .forEach { it.onDiscoverFeature += { sender, feature -> log("${feature.name.toTitleCase()} has been discovered on ${sender.name}.") } }
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

    private fun log(msg: String) {
        _log.pushFront(msg)
        if (_log.size > 20) {
            _log.popBack()
        }
    }
}