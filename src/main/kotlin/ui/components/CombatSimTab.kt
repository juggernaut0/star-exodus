package ui.components

import kui.Component
import ui.GameService

class CombatSimTab(private val gameService: GameService) : Component() {
    override fun render() {
        if (gameService.game.fleet.blockedState != null) {
            markup().p { +"Combat is in progress" }
        } else {
            markup().component(BattleSetup(gameService))
        }
    }
}