package ui.components

import kui.Component
import ui.*

class LogTabComponent(game: GameService) : Component() {
    private val logList = LogListComponent(game)

    override fun render() {
        markup().row {
            col12 {
                component(logList)
            }
        }
    }
}