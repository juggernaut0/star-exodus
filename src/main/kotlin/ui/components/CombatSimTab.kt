package ui.components

import game.BlockedState
import kui.Component
import kui.renderOnSet
import ui.GameService

class CombatSimTab(gameService: GameService) : Component() {
    var state: Component by renderOnSet(if (gameService.game.fleet.blockedState is BlockedState.Combat) CombatView(gameService, this) else BattleSetup(gameService, this))

    override fun render() {
        markup().component(state)
    }
}