package ui

import game.InventoryItem
import game.MiningTarget
import jQuery

@Suppress("MemberVisibilityCanPrivate", "unused")
class FleetController(val gameService: GameService) {
    var fleet: Array<ShipView> = emptyArray()
    var totalPopulation: Int = 0
    var fleetSpeed: Int = 0
    var selectedShip: ShipView? = null

    init {
        gameService.initWhenReady { _, _ ->
            refreshFleet()

            gameService.game.onTurn += { _, _ -> refreshFleet() }
        }
    }

    @JsName("refreshFleet")
    fun refreshFleet() {
        closeShipCollapse()
        fleet = gameService.game.fleet.ships.map { ShipView(it) }.toTypedArray()
        totalPopulation = gameService.game.fleet.ships.sumBy { it.crew }
        fleetSpeed = gameService.game.fleet.speed
    }

    @JsName("shipRowClass")
    fun shipRowClass(shipView: ShipView) = if (shipView == selectedShip) "table-primary" else ""

    @JsName("fuelCons")
    fun fuelCons(shipView: ShipView?): Int
            = if (shipView == null) 0 else gameService.game.fleet.fuelConsumptionAtSpeed(shipView.ship)

    @JsName("openShipCollapse")
    fun openShipCollapse(shipView: ShipView) {
        selectedShip = shipView
        jQuery("#shipDetails").collapse("show")
    }

    @JsName("closeShipCollapse")
    fun closeShipCollapse() {
        selectedShip = null
        jQuery("#shipDetails").collapse("hide")
    }

    @JsName("renameSelectedShip")
    fun renameSelectedShip(name: String) {
        selectedShip?.apply { ship.rename(name) }
    }

    @JsName("abandonSelectedShip")
    fun abandonSelectedShip() {
        selectedShip?.apply { gameService.game.fleet.abandonShip(ship) }
        refreshFleet()
    }

    @JsName("selectedShipExplore")
    fun selectedShipExplore(planet: PlanetView?) {
        selectedShip?.apply { ship.exploring = planet?.planet }
    }

    @JsName("selectedShipMine")
    fun selectedShipMine(planet: PlanetView?, resourceName: String?) {
        val mining = if (planet != null && resourceName != null) {
            MiningTarget(planet.planet, InventoryItem.valueOf(resourceName))
        } else {
            null
        }
        selectedShip?.apply { ship.mining = mining }
    }
}