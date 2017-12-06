package game

import serialization.Serializable
import util.*
import kotlin.math.min

class Planet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, exploration: Int) : Serializable, EventEmitter<Planet>() {
    val onDiscoverFeature = Event<Planet, PlanetFeature>().bind(this)

    var exploration: Int = exploration // out of 100
        private set

    val discoveredFeatures get() = features.subList(0, (exploration/20))

    val oreAmount: Int
        get() {
            val feats = discoveredFeatures
            return when {
                PlanetFeature.POOR_RESOURCE_DEPOSITS in feats -> 8
                PlanetFeature.RICH_RESOURCE_DEPOSITS in feats -> 16
                else -> 0
            }
        }

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
                    .filter { features[it] != PlanetFeature.NOTHING }
                    .forEach { onDiscoverFeature(features[it]) }
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

            /*
            int cap = 0;
            if(features.contains(PlanetFeature.COLONIZED_MOON)) cap += 750;
            else if(features.contains(PlanetFeature.SMALL_COLONY)) cap += 1500;
            else if(features.contains(PlanetFeature.LARGE_COLONY)) cap += 4000;
            else if(features.contains(PlanetFeature.HEAVILY_SETTLED)) cap += 10000;
            if(cap != 0){
                inventory = new Inventory(cap);
                inventory.addItems(InventoryItem.FOOD, random.nextInt((int) (cap*0.3)));
                inventory.addItems(InventoryItem.FUEL, random.nextInt((int) (cap*0.1)));
                inventory.addItems(InventoryItem.METAL, random.nextInt((int) (cap*0.1)));
                inventory.addItems(InventoryItem.GOODS, random.nextInt((int) (cap*0.15)));
                inventory.addItems(InventoryItem.METAL_ORE, random.nextInt((int) (cap*0.1)));
                inventory.addItems(InventoryItem.FUEL_ORE, random.nextInt((int) (cap*0.1)));
                inventory.addItems(InventoryItem.RARE_METALS, random.nextInt((int) (cap*0.05)));
                inventory.addItems(InventoryItem.TECHNOLOGY, random.nextInt((int) (cap*0.05)));
                for(InventoryItem item : InventoryItem.values()) demand.put(item, random.nextGaussian());
            }
             */

            return Planet(name, type, features, 0)
        }
    }
}
