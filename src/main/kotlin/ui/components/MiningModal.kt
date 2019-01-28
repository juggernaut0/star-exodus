package ui.components

import game.InventoryItem
import game.Ship
import kui.*
import ui.GameService
import ui.ItemView
import ui.PlanetView
import ui.ShipView

class MiningModal(
        private val gameService: GameService,
        private val ship: ShipView
) : Component() {
    private var miningTarget: PlanetView? by renderOnSet(null)
    private var miningResource: ItemView? by renderOnSet(gatherableItems[2])

    /*fun ok() {
        val planet = miningTarget
        val resource = miningResource
        ship.apply {
            ship.mining = if (planet != null && resource != null) {
                Ship.MiningTarget(planet.planet, resource.item)
            } else {
                null
            }
        }
    }*/

    override fun render() {
        markup().div {
            label {
                +"Planet"
                select(classes("form-control"), gameService.currentSystem.planets, model = ::miningTarget)
            }
            label {
                +"Resource"
                select(Props(classes = listOf("form-control"), disabled = miningTarget == null), gatherableItems, model = ::miningResource)
            }
            p { +"Expected yield: ${ship.miningYield(miningTarget, miningResource?.item)}" }
        }
    }

    companion object {
        private val gatherableItems = listOf(InventoryItem.FOOD, InventoryItem.FUEL, InventoryItem.METAL_ORE).map { ItemView(it) }
    }
}