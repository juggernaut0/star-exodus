package ui.components

import kui.Component
import kui.Props
import ui.*

class LogTabComponent(private val game: GameService) : Component() {
    private val logList = LogListComponent(game)

    override fun render() {
        markup().row {
            col9 {
                component(logList)
            }
            col3 {
                button(Props(classes = bsBtnBlock("success"), click = { game.game.nextDay() })) {
                    +"Next Day"
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