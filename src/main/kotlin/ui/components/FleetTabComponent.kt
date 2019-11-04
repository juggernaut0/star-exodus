package ui.components

import game.Fleet
import kui.Component
import kui.Props
import kui.classes
import ui.*
import util.toTitleCase

class FleetTabComponent(private val gameService: GameService) : Component() {
    private val shipDetailsComponent = ShipDetailsComponent(gameService)

    init {
        gameService.game.onTurn += { _, _ -> render() }
        gameService.onFleetUpdate += { _, _ -> render() }
    }

    private fun autoSupply() {
        gameService.game.fleet.autoSupply()
        gameService.invokeFleetUpdate()
    }

    private fun openShipDetail(shipView: ShipView) {
        shipDetailsComponent.selectedShip = shipView
        render()
    }

    private fun targetDist(): Int? = gameService.game.fleet.ftlTargetDestination?.distance

    private fun shipRowClass(shipView: ShipView) = when {
        shipView == shipDetailsComponent.selectedShip -> selectedRow
        shipView.ship.crew < shipView.ship.minCrew -> dangerRow
        shipView.isCriticalFood || shipView.lowFuel(targetDist() ?: 80) -> dangerRow
        shipView.isLowFood || shipView.isLowFuel -> warningRow
        else -> defaultRow
    }

    private fun ftlStatus(): String {
        with(gameService.game.fleet) {
            val cooldown = ftlCooldownTimeRemaining
            if (cooldown > 0) return "Cooling down ($cooldown days remaining)"
            val warmup = ftlWarmupTimeRemaining
            if (warmup > 0) return "Warming up ($warmup days remaining)"
            if (isFtlReady) return "Ready"
        }
        return "Unknown"
    }

    override fun render() {
        if (!gameService.ready) return
        val fleetView = FleetView(gameService.game.fleet)
        markup().div {
            div(classes("btn-group")) {
                if (fleetView.destination == null) {
                    button(Props(
                            id = "selectDestinationBtn", // Hack to force kui to reconstruct element because bootstrap attaches data to it
                            classes = listOf("btn", "btn-primary"),
                            attrs = bsModalToggle("destinationModal"),
                            disabled = !fleetView.isFtlReady
                    )) { +"Select Destination" }
                } else {
                    button(Props(
                            classes = listOf("btn", "btn-warning"),
                            click = { gameService.game.fleet.cancelFtl(); render() }
                    )) { +"Cancel FTL" }
                }
                button(Props(classes = listOf("btn", "btn-primary"), click = ::autoSupply)) { +"Auto-Supply Fleet" }
                button(Props(
                        classes = listOf("btn", "btn-primary", "dropdown-toggle"),
                        attrs = mapOf("data-toggle" to "dropdown")
                )) {
                    +"Gather Focus: ${fleetView.gatherFocus}"
                }
                div(classes("dropdown-menu")) {
                    for (gf in Fleet.GatherFocus.values()) {
                        a(Props(
                                classes = listOf("dropdown-item"),
                                click = { gameService.game.fleet.gatherFocus = gf }
                        )) { +gf.name.toTitleCase() }
                    }
                }
            }
            row {
                colMd4 { +"Ships: ${fleetView.numShips}" }
                colMd4 { +"Population: ${fleetView.totalPopulation}" }
                colMd4 { +"Speed: ${fleetView.speed}" }
            }
            row {
                colMd6 { +"FTL Status: ${ftlStatus()}" }
                colMd6 {
                    if (fleetView.destination == null) {
                        +"No destination set"
                    } else {
                        +"Destination: ${fleetView.destination.star.name} (${fleetView.destination.distance})"
                    }
                }
            }
            row {
                colMd3 {
                    for (group in gameService.game.fleet.groups) {
                        div(classes("card", "mb-2")) {
                            div(classes("card-header")) {
                                h5 { +group.name }
                            }
                            div(classes("list-group", "list-group-flush")) {
                                for (ship in group.ships.sortedBy { it.name }.map { ShipView(it) }) {
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
                    }

                }
                colMd9 {
                    component(shipDetailsComponent)
                }
            }

            val destinationModal = DestinationModal(gameService)
            component(Modal("destinationModal", "Select Destination", large = true, ok = { destinationModal.setDestination(); render() })) {
                slot(Unit) {
                    component(destinationModal)
                }
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