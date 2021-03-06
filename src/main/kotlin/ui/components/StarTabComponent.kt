package ui.components

import kui.Component
import kui.Props
import kui.classes
import ui.*

class StarTabComponent(private val gameService: GameService) : Component() {
    private val details = PlanetDetailsComponent(gameService)

    init {
        gameService.game.onTurn += { _, _ -> render() }
    }

    private fun openPlanetDetail(planetView: PlanetView) {
        details.selectedPlanet = planetView
        render()
    }

    private fun planetRowClass(planetView: PlanetView) = when {
        planetView == details.selectedPlanet -> selectedRow
        else -> defaultRow
    }

    override fun render() {
        markup().div {
            val star = gameService.currentSystem
            h4 {
                +"${star.name} "
                small { +star.type }
            }
            row {
                colMd3 {
                    div(classes("list-group")) {
                        for (planet in star.planets) {
                            button(Props(classes = planetRowClass(planet), click = { openPlanetDetail(planet) })) {
                                h5 {
                                    +planet.name
                                    small(classes("ml-2")) { +planet.type }
                                }
                                p {
                                    +"Explore: ${planet.exploration}"
                                }
                            }
                        }
                    }
                }
                colMd9 {
                    component(details)
                }
            }
        }
    }

    companion object {
        private val defaultRow = listOf("list-group-item", "list-group-item-action")
        private val selectedRow = listOf("list-group-item", "list-group-item-action", "list-group-item-primary")
    }
}