package ui

import game.ExodusGame
import game.StarSystem
import game.SystemArrivalEvent
import util.Deque
import util.event
import util.EventEmitter
import util.toTitleCase

class EventLog(gameService: GameService) : EventEmitter<EventLog>() {
    private val _messages = Deque<LogMessage>()
    val messages: Collection<LogMessage> get() = _messages

    val eventAdded = event<EventLog, String>()

    init {
        _messages.pushFront(LogMessage("Welcome to Star Exodus!", Style.NORMAL))

        gameService.onReady += { sender, _ ->
            registerGameListeners(sender.game)
        }
    }

    private fun registerGameListeners(game: ExodusGame) {
        game.onTurn += { sender, _ -> log("Day ${sender.day}", Style.INFO) }

        game.fleet.onArrive += { _, args ->
            log("The fleet has arrived in the ${args.star.name} system.")
            logArrivalEvent(args.arrivalEvent)
            registerPlanetListeners(args.star)
        }
        game.fleet.onTimerFinished += { _, event ->
            when (event) {
                SystemArrivalEvent.HOSTILE_CIVILIZATION -> log("Your time is up! The hostile empire in this system is attacking!", Style.DANGER)
                else -> { /* empty */ }
            }
        }

        registerPlanetListeners(game.fleet.currentLocation)

        for (ship in game.fleet.ships) {
            //ship.onMine += { sender, args -> log("${sender.name} has gathered ${args.amount} ${args.resource.name.toTitleCase()} from ${args.planet.name}.") }
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
                log(msg, Style.SUCCESS)
            }
        }
    }

    private fun logArrivalEvent(event: SystemArrivalEvent) {
        when (event) {
            SystemArrivalEvent.NOTHING -> { /* empty */ }
            SystemArrivalEvent.HOSTILE_CIVILIZATION ->
                log("This system is occupied by a hostile civilization. They demand you leave immediately or face their wrath!", Style.DANGER)
            SystemArrivalEvent.ATTACKED -> log("You are being attacked by alien vessels!", Style.DANGER)
            SystemArrivalEvent.PIRATES -> log("You are being attacked by a fierce band of pirates!", Style.DANGER)
            SystemArrivalEvent.BANDITS ->
                log("You are being hailed by a bandit fleet. They demand you hand over your cargo or face the consequences!", Style.DANGER)
            SystemArrivalEvent.DISTRESS_SIGNAL ->
                log("The fleet has picked up a faint distress signal originating in this system.", Style.WARNING)
        }
    }

    private fun log(msg: String, style: Style = Style.NORMAL) {
        _messages.pushFront(LogMessage(msg, style))
        if (_messages.size > 20) {
            _messages.popBack()
        }
        eventAdded(msg)
    }

    data class LogMessage(val text: String, val style: Style)
    enum class Style { NORMAL, SUCCESS, INFO, WARNING, DANGER }
}