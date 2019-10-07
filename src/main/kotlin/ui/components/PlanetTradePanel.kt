package ui.components

import game.Trade
import kui.Component
import kui.Props
import kui.classes
import ui.*
import util.Event

class PlanetTradePanel(private val gameService: GameService, private var planet: PlanetView) : Component() {
    private var tradeShip: ShipView = gameService.game.fleet.ships.first().let { ShipView(it) }
        set(value) {
            field = value
            shipInventory = value.inventory
            render()
        }
    private var planetInventory = planet.inventory
    private var shipInventory = tradeShip.inventory

    init {
        gameService.onFleetUpdate += Event.Handler("PlanetTradePanel") { _, _ ->
            planetInventory = planet.inventory
            shipInventory = tradeShip.inventory
            render()
        }
    }

    private fun tradeBalance(): Int {
        fun invValue(ic: InventoryContents) = ic.selected * ic.item.value
        return planetInventory.sumBy { invValue(it) } - shipInventory.sumBy { invValue(it) }
    }

    private fun trade() {
        //tradeShip.inventory.forEach { item -> console.log("${item.itemName}: ${item.selected}") }

        val trade = Trade(tradeShip.ship.inventory, planet.planet)
        for (ic in planetInventory) {
            trade.proposed[ic.item] = ic.selected
            ic.validClass = ""
        }
        for (ic in shipInventory) {
            trade.proposed[ic.item] = (trade.proposed[ic.item] ?: 0) - ic.selected
            ic.validClass = ""
        }

        if(trade.execute()) {
            planet = PlanetView(planet.planet)
            gameService.invokeFleetUpdate()
            render()
        }
    }

    override fun render() {
        markup().div {
            h5 { +"Trade" }
            row {
                col6 {
                    h6 { +"Planet Inventory" }
                }
                col6 {
                    select(classes("form-control"), gameService.game.fleet.ships.map { ShipView(it) }, ::tradeShip)
                }
            }
            row {
                col6 {
                    table(classes("table", "table-sm")) {
                        for (item in planetInventory) {
                            tr {
                                td { +item.itemName }
                                td { +item.count.toString() }
                                td { component(ValidatedIntInput(0..item.count, item::selected, this@PlanetTradePanel)) }
                            }
                        }
                    }
                }
                col6 {
                    p(classes("mt-2")) { +"Cargo space: ${tradeShip.cargo}" }
                    table(classes("table", "table-sm")) {
                        for (item in shipInventory) {
                            tr {
                                td { +item.itemName }
                                td { +item.count.toString() }
                                td { component(ValidatedIntInput(0..item.count, item::selected, this@PlanetTradePanel)) }
                            }
                        }
                    }
                }
            }
            val balance = tradeBalance()
            div(classes("text-center")) { +"Balance of Trade: $balance" }
            button(Props(classes = listOf("btn", "btn-primary", "btn-block"), click = ::trade, disabled = balance > 0)) { +"Trade" }
        }
    }
}