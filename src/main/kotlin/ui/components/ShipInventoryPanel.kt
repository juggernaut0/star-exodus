package ui.components

import game.InventoryItem
import kui.Component
import kui.Props
import kui.classes
import ui.*

class ShipInventoryPanel(private val gameService: GameService, private val selectedShip: ShipView) : Component() {
    private var target: ShipView? = null
    private var item: ItemView? = null
    private var amount: Int = 0

    private fun transfer() {
        val src = selectedShip.ship
        val dest = target?.ship
        val item = item?.item

        if (dest != null && item != null) {
            src.inventory.transferItemsTo(dest.inventory, item, amount)
            gameService.invokeFleetUpdate()
        }
    }

    override fun render() {
        markup().div {
            h5(classes("card-title")) { +"Inventory" }
            p { +"Capacity: ${selectedShip.cargo}" }
            table(classes("table", "table-sm")) {
                for (item in selectedShip.inventory) {
                    tr {
                        td { +item.itemName }
                        td { +item.count.toString() }
                    }
                }
            }
            h6 { +"Transfer" }
            label(classes("w-100")) {
                +"Transfer To"
                select(classes("form-control"), gameService.game.fleet.ships.filter { it != selectedShip.ship }.map { ShipView(it) }, "", ::target)
            }
            row {
                colMd6 {
                    label(classes("w-100")) {
                        +"Item"
                        select(classes("form-control"), InventoryItem.values().map { ItemView(it) }, "", ::item)
                    }
                }
                colMd6 {
                    label(classes("w-100")) {
                        +"Amount"
                        component(ValidatedIntInput(0..1000, ::amount, this@ShipInventoryPanel))
                    }
                }
            }
            button(Props(classes = listOf("btn", "btn-primary", "mt-2"), click = ::transfer)) { +"Transfer" }
        }
    }
}