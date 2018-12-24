package ui.components

import kui.Component
import kui.Props
import kui.classes
import ui.GameService
import ui.ShipView

class ShipTransferCrewPanel(private val gameService: GameService, private val selectedShip: ShipView) : Component() {
    private var target: ShipView? = null
    private var amount: Double = 0.0

    private fun transferCrew() {
        val src = selectedShip.ship
        val dest = target?.ship
        if (dest != null) {
            src.transferCrew(dest, amount.toInt())
            gameService.invokeFleetUpdate()
        }
    }

    override fun render() {
        markup().div {
            h5(classes("card-title")) { +"Transfer Crew" }
            label(classes("w-100")) {
                +"Transfer To"
                select(classes("form-control"), gameService.game.fleet.ships.filter { it != selectedShip.ship }.map { ShipView(it) }, "", ::target)
            }
            label(classes("w-100")) {
                +"Amount"
                inputNumber(classes("form-control"), ::amount)
            }
            button(Props(classes = listOf("btn", "btn-primary", "mt-2"), click = ::transferCrew)) { +"Transfer" }
        }
    }
}
