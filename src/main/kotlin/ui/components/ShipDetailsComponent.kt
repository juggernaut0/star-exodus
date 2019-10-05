package ui.components

import kui.*
import ui.*
import util.Event
import util.toPrecision

class ShipDetailsComponent(private val gameService: GameService) : Component() {
    var selectedShip: ShipView? = null

    private var newName: String = ""
    private var exploreTarget: PlanetView? = null

    init {
        gameService.game.fleet.onArrive += Event.Handler("ShipDetailsComponent") { _, _ ->
            exploreTarget = null
        }
    }

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

    private fun requiredFuel(): Int? {
        val ship = selectedShip ?: return null
        val dest = gameService.game.fleet.ftlTargetDestination ?: return null
        return ship.ship.fuelConsumption(dest.distance)
    }

    override fun render() {
        if (selectedShip?.let { it.ship in gameService.game.fleet.ships } == false) {
            selectedShip = null
        }
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
                    +"Fuel consumption: ${ship.ship.fuelConsumption.toPrecision(3)} / ly"
                    requiredFuel()?.let {
                        +" ($it required for next jump)"
                    }
                }
                p { +"Exploring: ${ship.exploring}" }
                p { +"Gathering: ${ship.mining} "}
                div(classes("btn-group")) {
                    button(Props(classes = bsBtnOutline("primary"),
                            attrs = bsModalToggle("shipRenameModal"),
                            click = { newName = "" })) {
                        +"Rename"
                    }
                    button(Props(classes = bsBtnOutline("primary"),
                            attrs = bsModalToggle("shipExploreModal"))) {
                        +"Explore Planet"
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
                    slot(Unit) {
                        label {
                            +"New Name"
                            inputText(classes("form-control"), model = ::newName)
                        }
                    }
                }

                component(Modal("shipExploreModal", "Explore Planet", ok = { setExplore(ship) })) {
                    slot(Unit) {
                        p { +"This ship can send ${ship.explorers} explorers." }
                        label {
                            +"Planet"
                            val exploreTargets = gameService.currentSystem.planets.filterNot { it.isExplored }
                            select(classes("form-control"), exploreTargets, model = ::exploreTarget, nullOption = "None")
                        }
                    }
                }

                component(Modal("shipAbandonModal", "Abandon Ship", danger = true, ok = { abandon(ship) })) {
                    slot(Unit) {
                        +"You will lose this ship forever. Are you sure?"
                    }
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
