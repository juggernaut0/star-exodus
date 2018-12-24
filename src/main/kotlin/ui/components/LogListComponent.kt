package ui.components

import kui.Component
import kui.classes
import ui.GameService

class LogListComponent(gameService: GameService) : Component() {
    private val eventLog = gameService.eventLog

    init {
        gameService.onReady += { _, _ -> render() }
        eventLog.eventAdded += { _, _ -> render() }
    }

    override fun render() {
        markup().ul(classes("list-group")) {
            for (msg in eventLog.messages) {
                li(classes("list-group-item")) {
                    +msg
                }
            }
        }
    }
}
