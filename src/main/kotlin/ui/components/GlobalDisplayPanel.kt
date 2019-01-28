package ui.components

import kui.Component
import kui.Props
import kui.classes
import ui.*

class GlobalDisplayPanel(private val gameService: GameService) : Component() {
    init {
        gameService.game.onTurn += { _, _ -> render() }
    }

    override fun render() {
        markup().div {
            row {
                col6 {
                    +"Day ${gameService.game.day}"
                }
                col6 {
                    div(classes("btn-group", "float-right")) {
                        when {
                            gameService.game.canNextDay() -> {
                                button(Props(classes = bsBtn("success"), click = { gameService.game.nextDay() })) {
                                    +"Next Day"
                                }
                            }
                            gameService.game.inCombat -> {
                                button(Props(classes = bsBtn("danger"), click = { gameService.game.fleet.endBlocker(); render() })) {
                                    +"Start Combat"
                                }
                            }
                            else -> {
                                button(Props(classes = bsBtn("warning"), click = { gameService.game.fleet.endBlocker(); render() })) {
                                    +"Answer Hail"
                                }
                            }
                        }
                        button(Props(classes = bsBtn("primary"), click = { gameService.saveGame() })) {
                            +"Save Game"
                        }
                        button(Props(classes = bsBtn("warning"), attrs = bsModalToggle("clearSaveModal"))) {
                            +"Clear Saved Game"
                        }
                    }
                }
            }
            component(Modal("clearSaveModal", "Clear Saved Game", danger = true, ok = { gameService.clearSavedGame() })) {
                +"This will erase your saved game. Are you sure?"
            }
        }
    }
}