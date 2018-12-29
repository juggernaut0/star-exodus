package ui

import game.Fleet

class FleetView(fleet: Fleet) {
    val ships = fleet.ships.map { ShipView(it) }.toTypedArray()
    val totalPopulation = fleet.ships.sumBy { it.crew }
    val speed = fleet.speed
    val isFtlReady = fleet.isFtlReady
    val destination = fleet.ftlTargetDestination
}