package ui.components

import kui.Component
import kui.Props
import ui.*

class LogTabComponent(private val game: GameService) : Component() {
    private val logList = LogListComponent(game)

    init {
        game.game.onTurn += { _, _ -> render() }
    }

    override fun render() {
        markup().row {
            col9 {
                component(logList)
            }
            col3 {
                when {
                    game.game.canNextDay() -> {
                        button(Props(classes = bsBtnBlock("success"), click = { game.game.nextDay() })) {
                            +"Next Day"
                        }
                    }
                    game.game.inCombat -> {
                        button(Props(classes = bsBtnBlock("danger"), click = { game.game.fleet.endBlocker(); render() })) {
                            +"Start Combat"
                        }
                    }
                    else -> {
                        button(Props(classes = bsBtnBlock("warning"), click = { game.game.fleet.endBlocker(); render() })) {
                            +"Answer Hail"
                        }
                    }
                }
                button(Props(classes = bsBtnBlock("primary"), click = { game.saveGame() })) {
                    +"Save Game"
                }
                button(Props(classes = bsBtnBlock("warning"), attrs = bsModalToggle("clearSaveModal"))) {
                    +"Clear Saved Game"
                }
            }
            component(Modal("clearSaveModal", "Clear Saved Game", danger = true, ok = { game.clearSavedGame() })) {
                +"This will erase your saved game. Are you sure?"
            }
        }
    }
}