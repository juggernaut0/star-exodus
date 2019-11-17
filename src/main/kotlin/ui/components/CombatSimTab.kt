package ui.components

import kui.Component
import kui.renderOnSet
import ui.GameService

class CombatSimTab(private val gameService: GameService) : Component() {
    var state: Component by renderOnSet(BattleSetup(gameService, this))

    override fun render() {
        markup().component(state)
    }
}