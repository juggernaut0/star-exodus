package ui.components

import game.BlockedState
import game.InventoryItem
import kui.Component
import kui.Props
import kui.classes
import ui.*

class GlobalDisplayPanel(private val gameService: GameService) : Component() {
    init {
        gameService.game.onTurn += { _, _ -> render() }
        gameService.onFleetUpdate += { _, _ -> render() }
    }

    private fun total(item: InventoryItem) = gameService.game.fleet.ships.sumBy { it.inventory[item] }
    private fun gathering(item: InventoryItem): Int {
        return gameService.game.fleet.ships
                .asSequence()
                .filter { it.mining?.resource == item }
                .sumBy { it.miningYield(it.mining!!) }
    }
    private fun foodProduction(): Int {
        return gathering(InventoryItem.FOOD) + gameService.game.fleet.ships.sumBy { it.shipClass.foodProduction }
    }
    private fun foodConsumption(): Int {
        return gameService.game.fleet.ships.sumBy { it.foodConsumption }
    }
    private fun daysOfFood(): Int {
        return gameService.game.fleet.ships
                .asSequence()
                .map { it.inventory[InventoryItem.FOOD] / it.foodConsumption }
                .min() ?: 0
    }
    private fun distanceOfFuel(): Int {
        return gameService.game.fleet.ships
                .asSequence()
                .map { it.inventory[InventoryItem.FUEL] / it.fuelConsumption }
                .min()?.toInt() ?: 0
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
                            gameService.game.inCombat && gameService.viewState == GameService.ViewState.MAIN -> {
                                button(Props(classes = bsBtn("danger"), click = { gameService.viewState = GameService.ViewState.COMBAT })) {
                                    +"Start Combat"
                                }
                            }
                            gameService.game.fleet.blockedState is BlockedState.Hailed -> {
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
            row {
                col6 {
                    +"Food: ${total(InventoryItem.FOOD)} (+${foodProduction()}/-${foodConsumption()}) $EM ${daysOfFood()} days"
                }
                col6 {
                    +"Fuel: ${total(InventoryItem.FUEL)} (+${gathering(InventoryItem.FUEL)}) $EM ${distanceOfFuel()} ly"
                }
            }
            component(Modal("clearSaveModal", "Clear Saved Game", danger = true, ok = { gameService.clearSavedGame() })) {
                slot(Unit) {
                    +"This will erase your saved game. Are you sure?"
                }
            }
        }
    }
}