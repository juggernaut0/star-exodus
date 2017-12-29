package game

import util.Random
import kotlin.math.min

interface PlanetFeatureAction {
    fun perform(planet: Planet, ships: List<Ship>): PlanetFeatureActionResult
}

class PlanetFeatureActionResult(
        val resources: Map<InventoryItem, Int> = emptyMap()
)

class OreAction(private val minAmt: Int, private val maxAmt: Int) : PlanetFeatureAction {
    override fun perform(planet: Planet, ships: List<Ship>): PlanetFeatureActionResult {
        val mostSpace = ships.asSequence().map { it.inventory.freeSpace }.max() ?: 0
        val amt = min(Random.range(minAmt, maxAmt + 1), mostSpace)

        val fuelAmt = Random.range(amt / 2)
        val rareAmt = if (planet.discoveredFeatures.contains(PlanetFeature.RARE_ELEMENTS)) Random.range((amt - fuelAmt) / 2) else 0
        val metalAmt = amt - fuelAmt - rareAmt

        val hasSpace = ships.find { it.inventory.freeSpace >= amt }!! // amt must be <= mostSpace
        val result = mutableMapOf<InventoryItem, Int>()
        with(hasSpace.inventory) {
            result[InventoryItem.FUEL_ORE] = addItems(InventoryItem.FUEL_ORE, fuelAmt)
            result[InventoryItem.RARE_METALS] = addItems(InventoryItem.RARE_METALS, rareAmt)
            result[InventoryItem.METAL_ORE] = addItems(InventoryItem.METAL_ORE, metalAmt)
        }
        return PlanetFeatureActionResult(result)
    }
}

class ResourceAction(private val item: InventoryItem, private val minAmt: Int, private val maxAmt: Int) : PlanetFeatureAction {
    override fun perform(planet: Planet, ships: List<Ship>): PlanetFeatureActionResult {
        val mostSpace = ships.asSequence().map { it.inventory.freeSpace }.max() ?: 0
        val amt = min(Random.range(minAmt, maxAmt + 1), mostSpace)

        val hasSpace = ships.find { it.inventory.freeSpace >= amt }!! // amt must be <= mostSpace
        val actual = hasSpace.inventory.addItems(item, amt)
        return PlanetFeatureActionResult(mapOf(item to actual))
    }
}
