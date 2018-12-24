package ui.components

import kui.Component
import kui.Props
import kui.classes
import ui.*

class FleetTabComponent(private val gameService: GameService) : Component() {
    private val shipDetailsComponent = ShipDetailsComponent(gameService)

    init {
        gameService.game.onTurn += { _, _ -> render() }
        gameService.onFleetUpdate += { _, _ -> render() }
    }

    private fun autoSupply() {
        gameService.game.fleet.autoSupply()
        render()
    }

    private fun openShipDetail(shipView: ShipView) {
        shipDetailsComponent.selectedShip = shipView
        render()
    }

    private fun shipRowClass(shipView: ShipView) = when {
        shipView == shipDetailsComponent.selectedShip -> selectedRow
        shipView.ship.crew < shipView.ship.minCrew -> dangerRow
        shipView.lowFood(1) || shipView.lowFuel(1, gameService.game.fleet) -> dangerRow
        shipView.lowFood(3) || shipView.lowFuel(3, gameService.game.fleet) -> warningRow
        else -> defaultRow
    }

    override fun render() {
        if (!gameService.ready) return
        val fleetView = FleetView(gameService.game.fleet)
        markup().div {
            div(classes("btn-group")) {
                button(Props(classes = listOf("btn", "btn-primary"), attrs = bsModalToggle("destinationModal"))) { +"Select Destination" }
                button(Props(classes = listOf("btn", "btn-primary"), click = ::autoSupply)) { +"Auto-Supply Fleet" }
            }
            row {
                colMd4 { +"Ships: ${fleetView.ships.size}" }
                colMd4 { +"Population: ${fleetView.totalPopulation}" }
                colMd4 { +"Speed: ${fleetView.speed}" }
            }
            row {
                colMd3 {
                    div(classes("list-group")) {
                        for (ship in fleetView.ships.sortedBy { it.name }) {
                            button(Props(classes = shipRowClass(ship), click = { openShipDetail(ship) })) {
                                h5 {
                                    +ship.name
                                    small(classes("ml-2")) { +ship.shipClass }
                                }
                                row {
                                    // TODO icons?
                                    col6 { +"HP: ${ship.hull}" }
                                    col6 { +"Crew: ${ship.crew}" }
                                }
                            }
                        }
                    }
                }
                colMd9 {
                    component(shipDetailsComponent)
                }
            }

            val destinationModal = DestinationModal(gameService)
            component(Modal("destinationModal", "Select Destination", ok = { destinationModal.setDestination() })) {
                component(destinationModal)
            }
        }
    }

    companion object {
        private val defaultRow = listOf("list-group-item", "list-group-item-action")
        private val selectedRow = listOf("list-group-item", "list-group-item-action", "list-group-item-primary")
        private val warningRow = listOf("list-group-item", "list-group-item-action", "list-group-item-warning")
        private val dangerRow = listOf("list-group-item", "list-group-item-action", "list-group-item-danger")
    }
}