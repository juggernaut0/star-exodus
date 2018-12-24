package ui.components

import kui.Component
import kui.Props
import kui.classes
import ui.GameService
import ui.StarView
import ui.colMd6
import ui.row
import util.MutVector2

class DestinationModal(private val gameService: GameService) : Component() {
    private val nearbyStars = gameService.findNearbyStars()

    private var selectedStar: StarView? = null
        set(value) {
            field = value
            if (value != null) {
                customDestination = value.location.toMutVector()
            }
            render()
        }
    private var customDestination: MutVector2 = gameService.game.fleet.destination.toMutVector()

    fun setDestination() {
        gameService.game.fleet.destination = selectedStar?.location ?: customDestination.toIntVector()
    }

    override fun render() {
        markup().div {
            label {
                +"Nearby Stars"
                select(classes("form-control"), nearbyStars, "Custom", ::selectedStar)
            }
            label { +"Coordinates" }
            row {
                colMd6 {
                    inputNumber(Props(classes = listOf("form-control"), disabled = selectedStar != null), customDestination::x)
                }
                colMd6 {
                    inputNumber(Props(classes = listOf("form-control"), disabled = selectedStar != null), customDestination::y)
                }
            }
        }
    }
}