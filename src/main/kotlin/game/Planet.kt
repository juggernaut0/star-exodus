package game

import serialization.Serializable
import util.*
import kotlin.math.min

class Planet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, exploration: Int, val inventory: Inventory) : Serializable, EventEmitter<Planet>() {
    val onDiscoverFeature = Event<Planet, DiscoverFeatureEventArgs>().bind(this)

    var exploration: Int = exploration // out of 100
        private set

    val discoveredFeatures get() = features.subList(0, (exploration/20))
    val tradable get() = discoveredFeatures.find { it.tradeCapacity > 0 } != null

    val oreAmount: Int get() = discoveredFeatures.sumBy { it.oreAmt }

    fun explore(ships: List<Ship>) {
        if (ships.isEmpty()) return

        val numExplorers = ships.sumBy { it.explorers }
        val begin = exploration
        exploration = min(exploration + numExplorers / 5, 100)
        val end = exploration
        if(begin % 20 > end % 20 || end - begin >= 20){
            val n = begin / 20
            val l = end / 20
            (n until l)
                    .asSequence()
                    .map { features[it] }
                    .filter { it != PlanetFeature.NOTHING }
                    .forEach {
                        val result = it.discoveryAction?.perform(this, ships)
                        onDiscoverFeature(DiscoverFeatureEventArgs(it, result))
                    }
        }

        if (exploration == 100) {
            ships.forEach { it.exploring = null }
        }
    }

    companion object {
        operator fun invoke(name: String, type: PlanetType): Planet {
            val features = mutableListOf<PlanetFeature>()
            while (features.size < 5) {
                val feat = Random.choice(type.features)
                if (feat == PlanetFeature.NOTHING || feat !in features) {
                    features.add(feat)
                }
            }
            features.shuffle()

            val cap = features.sumBy { it.tradeCapacity }
            val inv = Inventory(cap)
            if (cap > 0) {
                inv.addItems(InventoryItem.FOOD, Random.range(cap * 0.3).toInt())
                inv.addItems(InventoryItem.FUEL, Random.range(cap * 0.1).toInt())
                inv.addItems(InventoryItem.METAL, Random.range(cap * 0.1).toInt())
                inv.addItems(InventoryItem.GOODS, Random.range(cap * 0.15).toInt())
                inv.addItems(InventoryItem.METAL_ORE, Random.range(cap * 0.1).toInt())
                inv.addItems(InventoryItem.FUEL_ORE, Random.range(cap * 0.1).toInt())
                inv.addItems(InventoryItem.RARE_METALS, Random.range(cap * 0.05).toInt())
                inv.addItems(InventoryItem.TECHNOLOGY, Random.range(cap * 0.05).toInt())
            }

            return Planet(name, type, features, 0, inv)
        }
    }

    class DiscoverFeatureEventArgs(val feature: PlanetFeature, val result: PlanetFeatureActionResult?)
}
