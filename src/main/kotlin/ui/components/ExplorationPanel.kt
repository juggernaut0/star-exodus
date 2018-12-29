package ui.components

import kui.Component
import kui.Props
import kui.classes
import ui.GameService
import ui.PlanetView

class ExplorationPanel(private val gameService: GameService, private val planet: PlanetView) : Component() {
    private fun currentExplorers(): Int? {
        return gameService.game.fleet.ships
                .asSequence()
                .filter { it.exploring == planet.planet }
                .sumBy { it.explorers }
    }

    override fun render() {
        markup().div {
            h5 { +"Exploration" }
            p { +"Explorers: ${currentExplorers()}" }
            h6 { +"Progress" }
            div(classes("progress", "position-relative", "my-3")) {
                div(Props(classes = listOf("progress-bar"), attrs = mapOf("style" to "width: ${planet.exploration}"))) {
                    +planet.exploration
                }
                div(classes("segments")) {
                    div(classes("segment")) {}
                    div(classes("segment")) {}
                    div(classes("segment")) {}
                    div(classes("segment")) {}
                }
            }
            h6 { +"Features" }
            ul(classes("list-group")) {
                for (feat in planet.features) {
                    li(classes("list-group-item", "flex-column")) {
                        h5(classes("mt-1")) { +feat.name }
                        if (feat.description != null) {
                            p { +feat.description }
                        }
                    }
                }
            }
        }
    }
}