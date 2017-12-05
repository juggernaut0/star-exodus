package ui

import game.InventoryItem
import game.Ship
import jQuery
import util.MutVector2
import util.toTitleCase
import util.toTypedArray

@Suppress("MemberVisibilityCanPrivate", "unused")
class FleetController(val gameService: GameService) {
    val allItems = InventoryItem.values()

    var fleet: FleetView? = null
    var selectedShip: ShipView? = null
    var nearbyStars: Array<StarView> = emptyArray()
    var selectedDestination: MutVector2? = null
        set(value) {
            if (value != null) {
                customDestination = value
            }
            field = value
        }
    var customDestination: MutVector2 = MutVector2()

    var crewTransferTarget: ShipView? = null
    var crewTransferAmount: Int? = 0

    var cargoTransferTarget: ShipView? = null
    var cargoTransferItem: InventoryItem = InventoryItem.FOOD
    var cargoTransferAmount: Int? = 0

    init {
        gameService.onReady += { _, _ ->
            refreshFleet()

            gameService.game.onTurn += { _, _ -> refreshFleet() }
        }
    }

    @JsName("refreshFleet")
    fun refreshFleet() {
        val ship = selectedShip?.ship
        val cargoTarget = cargoTransferTarget?.ship
        fleet = FleetView(gameService.game.fleet)
                .also {
                    crewTransferTarget = it.ships[0]
                    cargoTransferTarget = it.ships.find { it.ship == cargoTarget }
                    selectedShip = it.ships.find { it.ship == ship }
                }
    }

    @JsName("shipRowClass")
    fun shipRowClass(shipView: ShipView) = when {
        shipView == selectedShip -> "table-primary"
        shipView.ship.crew < shipView.ship.minCrew -> "table-danger"
        shipView.lowFood(1) || shipView.lowFuel(1, gameService.game.fleet) -> "table-danger"
        shipView.lowFood(3) || shipView.lowFuel(3, gameService.game.fleet) -> "table-warning"
        else -> ""
    }

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

    @JsName("transferCrew")
    fun transferCrew() {
        val src = selectedShip?.ship
        val dest = crewTransferTarget?.ship
        val amt = crewTransferAmount

        if (src != null && dest != null && amt != null) {
            src.transferCrew(dest, amt)
            jQuery("#shipTransferCrew").collapse("hide")
        }
    }

    @JsName("transferCargo")
    fun transferCargo() {
        val src = selectedShip?.ship
        val dest = cargoTransferTarget?.ship
        val amt = cargoTransferAmount

        if (src != null && dest != null && amt != null) {
            src.inventory.transferItemsTo(dest.inventory, cargoTransferItem, amt)
            refreshFleet()
        }
    }

    @JsName("abandonSelectedShip")
    fun abandonSelectedShip() {
        selectedShip?.apply { gameService.game.fleet.abandonShip(ship) }
        refreshFleet()
        closeShipCollapse()
    }

    @JsName("selectedShipExplore")
    fun selectedShipExplore(planet: PlanetView?) {
        selectedShip?.apply { ship.exploring = planet?.planet }
    }

    @JsName("selectedShipMine")
    fun selectedShipMine(planet: PlanetView?, resourceName: String?) {
        selectedShip?.apply {
            ship.mining = if (planet != null && resourceName != null) {
                Ship.MiningTarget(planet.planet, InventoryItem.valueOf(resourceName))
            } else {
                null
            }
        }
    }

    @JsName("setDestination")
    fun setDestination() {
        gameService.game.fleet.destination = (selectedDestination ?: customDestination).toIntVector()
    }

    @JsName("resetSelectedDestination")
    fun resetSelectedDestination() {
        nearbyStars = gameService.game.galaxy.getNearbyStars(gameService.game.fleet.location, gameService.game.fleet.speed * 10.0)
                .asSequence()
                .filter { it.location != gameService.game.fleet.location }
                .map { StarView(it) }
                .toTypedArray()
        customDestination = gameService.game.fleet.destination.toMutVector()
    }

    @JsName("autoSupply")
    fun autoSupply() {
        gameService.game.fleet.autoSupply()
        refreshFleet()
    }

    @JsName("displayName")
    fun displayName(item: InventoryItem): String = item.name.toTitleCase()
}