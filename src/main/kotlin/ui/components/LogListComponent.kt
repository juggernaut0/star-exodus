package ui.components

import kui.Component
import kui.classes
import ui.EventLog
import ui.GameService

class LogListComponent(gameService: GameService) : Component() {
    private val eventLog = gameService.eventLog

    init {
        gameService.onReady += { _, _ -> render() }
        eventLog.eventAdded += { _, _ -> render() }
    }

    private fun itemClasses(style: EventLog.Style) = when(style) {
        EventLog.Style.NORMAL -> classes("list-group-item")
        EventLog.Style.SUCCESS -> classes("list-group-item", "list-group-item-success")
        EventLog.Style.INFO -> classes("list-group-item", "list-group-item-info")
        EventLog.Style.WARNING -> classes("list-group-item", "list-group-item-warning")
        EventLog.Style.DANGER -> classes("list-group-item", "list-group-item-danger")
    }

    override fun render() {
        markup().ul(classes("list-group")) {
            for (msg in eventLog.messages) {
                li(itemClasses(msg.style)) {
                    +msg.text
                }
            }
        }
    }
}
