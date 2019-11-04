package ui

import game.Fleet
import util.toTitleCase

class FleetView(fleet: Fleet) {
    val numShips = fleet.ships.count()
    val totalPopulation = fleet.ships.sumBy { it.crew }
    val speed = fleet.speed
    val isFtlReady = fleet.isFtlReady
    val destination = fleet.ftlTargetDestination
    val gatherFocus = fleet.gatherFocus.name.toTitleCase()
}