package ui.components

import kui.Component
import kui.classes
import ui.GameService
import ui.PlanetView

class PlanetDetailsComponent(private val gameService: GameService) : Component() {
    var selectedPlanet: PlanetView? = null

    override fun render() {
        markup().div {
            val planet = selectedPlanet
            if (planet != null) {
                h4 {
                    +"${planet.name} "
                    small(classes("text-muted")) { +planet.type }
                }
                div(classes("card", "mt-2")) {
                    div(classes("card-body")) {
                        component(ExplorationPanel(gameService, planet))
                    }
                }
                if (planet.tradable) {
                    div(classes("card", "mt-2")) {
                        div(classes("card-body")) {
                            component(PlanetTradePanel(gameService, planet))
                        }
                    }
                }
            } else {
                p { +"Select a planet to view details." }
            }
        }
    }
}