package ui.components

import game.Trade
import kui.Component
import kui.Props
import kui.classes
import kui.renderOnSet
import ui.*

class PlanetTradePanel(private val gameService: GameService, private var planet: PlanetView) : Component() {
    private var tradeShip: ShipView by renderOnSet(gameService.game.fleet.ships.first().let { ShipView(it) })

    private fun tradeBalance(): Int {
        fun invValue(ic: ShipView.InventoryContents) = ic.selected * ic.item.value
        return planet.inventory.sumBy { invValue(it) } - tradeShip.inventory.sumBy { invValue(it) }
    }

    fun trade() {
        //tradeShip.inventory.forEach { item -> console.log("${item.itemName}: ${item.selected}") }

        val trade = Trade(tradeShip.ship.inventory, planet.planet)
        for (ic in planet.inventory) {
            trade.proposed[ic.item] = ic.selected
            ic.validClass = ""
        }
        for (ic in tradeShip.inventory) {
            trade.proposed[ic.item] = (trade.proposed[ic.item] ?: 0) - ic.selected
            ic.validClass = ""
        }

        if(trade.execute()) {
            planet = PlanetView(planet.planet)
            gameService.invokeFleetUpdate()
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
                        for (item in planet.inventory) {
                            tr {
                                td { +item.itemName }
                                td { +item.count.toString() }
                                td { component(ValidatedIntInput(0..item.count, item::selected, this@PlanetTradePanel)) }
                            }
                        }
                    }
                }
                col6 {
                    table(classes("table", "table-sm")) {
                        for (item in tradeShip.inventory) {
                            tr {
                                td { +item.itemName }
                                td { +item.count.toString() }
                                td { component(ValidatedIntInput(0..item.count, item::selected, this@PlanetTradePanel)) }
                            }
                        }
                    }
                }
            }
            div(classes("text-center")) { +"Balance of Trade: ${tradeBalance()}" }
            button(Props(classes = listOf("btn", "btn-primary", "btn-block"), click = ::trade)) { +"Trade" }
        }
    }
}