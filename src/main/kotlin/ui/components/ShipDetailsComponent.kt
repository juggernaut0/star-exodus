package ui.components

import kui.*
import ui.*

class ShipDetailsComponent(private val gameService: GameService) : Component() {
    var selectedShip: ShipView? = null

    private var newName: String = ""
    private var exploreTarget: PlanetView? = null

    private fun setExplore(ship: ShipView) {
        ship.ship.exploring = exploreTarget?.planet
        gameService.invokeFleetUpdate()
    }

    private fun rename(ship: ShipView) {
        ship.ship.rename(newName)
        gameService.invokeFleetUpdate()
    }

    private fun abandon(ship: ShipView) {
        gameService.game.fleet.abandonShip(ship.ship)
        selectedShip = null
        gameService.invokeFleetUpdate()
    }

    override fun render() {
        markup().div {
            val ship = selectedShip
            if (ship != null) {
                h4 {
                    +"${ship.name} "
                    small(classes("text-muted")) { +ship.shipClass }
                }
                p { +"Food consumption: ${ship.foodCons} / day" }
                if (ship.foodProd > 0) p { +"Food production: ${ship.foodProd} / day" }
                p {
                    span(Props(title = "At current speed")) { +"Fuel Consumption" }
                    +": ${gameService.game.fleet.fuelConsumptionAtSpeed(ship.ship)} / day"
                }
                p { +"Exploring: ${ship.exploring}" }
                p { +"Mining: ${ship.mining} "}
                div(classes("btn-group")) {
                    button(Props(classes = bsBtnOutline("primary"),
                            attrs = bsModalToggle("shipRenameModal"),
                            click = { newName = "" })) {
                        +"Rename"
                    }
                    button(Props(classes = bsBtnOutline("primary"),
                            attrs = bsModalToggle("shipExploreModal"),
                            disabled = gameService.currentSystem == null)) {
                        +"Explore Planet"
                    }
                    button(Props(classes = bsBtnOutline("primary"),
                            attrs = bsModalToggle("shipMineModal"),
                            disabled = gameService.currentSystem == null)) {
                        +"Gather Resources"
                    }
                    button(Props(classes = bsBtnOutline("danger"), attrs = bsModalToggle("shipAbandonModal"))) {
                        +"Abandon"
                    }
                }

                div(classes("card", "mt-2")) {
                    div(classes("card-body")) {
                        component(ShipTransferCrewPanel(gameService, ship))
                    }
                }

                div(classes("card", "mt-2")) {
                    div(classes("card-body")) {
                        component(ShipInventoryPanel(gameService, ship))
                    }
                }

                div(classes("card", "mt-2")) {
                    div(classes("card-body")) {
                        h5(classes("card-title")) { +"Weapons" } // TODO weapons panel
                    }
                }

                component(Modal("shipRenameModal", "Rename Ship", ok = { rename(ship) })) {
                    label {
                        +"New Name"
                        inputText(classes("form-control"), ::newName)
                    }
                }

                component(Modal("shipExploreModal", "Explore Planet", ok = { setExplore(ship) })) {
                    p { +"This ship can send ${ship.explorers} explorers." }
                    label {
                        +"Planet"
                        select(classes("form-control"), gameService.currentSystem?.planets ?: emptyList(), model = ::exploreTarget, nullOption = "None")
                    }
                }

                val miningModal = MiningModal(gameService, ship)
                component(Modal("shipMineModal", "Gather Resources", ok = { miningModal.ok() })) {
                    component(miningModal)
                }

                component(Modal("shipAbandonModal", "Abandon Ship", danger = true, ok = { abandon(ship) })) {
                    +"You will lose this ship forever. Are you sure?"
                }
            } else {
                p { +"Select a ship to view details." }
            }
        }
    }

    private data class ExplorePlanetOption(val planet: PlanetView?) {
        override fun toString(): String = planet?.toString() ?: "None"
    }
}
