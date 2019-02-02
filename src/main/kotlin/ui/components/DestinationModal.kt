package ui.components

import kui.Component
import kui.Props
import kui.classes
import kui.renderOnSet
import ui.*

class DestinationModal(private val gameService: GameService) : Component() {
    private val destinations: List<StarView> = gameService.game.fleet.destinations.map { it.toView() }

    private var selectedStar: Int by renderOnSet(0)

    fun setDestination() {
        gameService.game.fleet.startFtl(selectedStar)
    }

    private fun listItemClass(i: Int): List<String> {
        return if (i == selectedStar) listOf("list-group-item", "list-group-item-action", "active")
        else listOf("list-group-item", "list-group-item-action")
    }

    override fun render() {
        markup().div(classes("list-group")) {
            for ((i, star) in destinations.withIndex()) {
                button(Props(classes = listItemClass(i), click = { selectedStar = i })) {
                    h5 { +star.name }
                    row {
                        colMd4 { +star.type }
                        colMd4 { +"${star.planets.size} planets" }
                        colMd4 { +"Distance: ${star.distance} ly" }
                    }
                }
            }
        }
    }
}