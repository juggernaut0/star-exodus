package game

import serialization.Serializable
import util.Random
import kotlin.js.Math

class Ship(
        name: String,
        val shipClass: ShipClass,
        hullPoints: Int,
        crew: Int,
        val inventory: Inventory,
        var exploring: Planet?,
        var mining: MiningTarget?
) : Serializable {
    var name: String = name
        private set

    var hullPoints: Int = hullPoints
        private set
    var crew: Int = crew
        private set

    val mass get() = shipClass.maxCrew/2 + inventory.freeSpace/2 + inventory.usedSpace + shipClass.hanger*2

    // fuel per distance unit per turn
    val fuelConsumption get() = Math.sqrt(mass.toDouble()) * FUEL_COEFFICIENT
    // food per turn
    val foodConsumption get() = Math.ceil(crew * FOOD_COEFFICIENT)

    val explorers get() = Math.min(Math.floor(0.1 * crew), 50)

    val destroyed get() = hullPoints == 0

    fun rename(newName: String) {
        if (newName.isNotBlank()){
            name = newName
        }
    }

    // returns amount actually changed
    fun modHullPoints(amt: Int): Int {
        val oldHp = hullPoints
        hullPoints = minOf(maxOf(hullPoints + amt, 0), shipClass.maxHull)
        return hullPoints - oldHp
    }

    fun modCrew(amt: Int): Int {
        val oldCrew = crew
        crew = minOf(maxOf(crew + amt, 0), shipClass.maxCrew)
        return crew - oldCrew
    }

    internal fun eatFood() {
        val curFood = inventory[InventoryItem.FOOD]
        if (curFood >= foodConsumption) {
            inventory.removeItems(InventoryItem.FOOD, foodConsumption)
        } else {
            val toKill = crew - (curFood / FOOD_COEFFICIENT).toInt()
            modCrew(-toKill)
            inventory.removeItems(InventoryItem.FOOD, curFood)
        }
    }

    fun miningYield(target: MiningTarget) = when (target.resource) {
        InventoryItem.FUEL -> target.planet.type.fuelGatherAmount * shipClass.fuelGatherMultiplier
        InventoryItem.FUEL_ORE, InventoryItem.METAL_ORE -> target.planet.oreAmount * shipClass.oreMultiplier
        InventoryItem.FOOD -> target.planet.type.foodGatherAmount * shipClass.foodGatherMutliplier
        else -> 0.0
    }.toInt()

    internal fun mine() {
        mining?.run {
            val amt = miningYield(this)

            if (resource == InventoryItem.FUEL_ORE || resource == InventoryItem.METAL_ORE) {
                val fuelAmt = Random.range(amt / 2)
                val rareAmt = if (planet.discoveredFeatures.contains(PlanetFeature.RARE_ELEMENTS)) Random.range((amt - fuelAmt) / 2) else 0
                val metalAmt = amt - fuelAmt - rareAmt
                inventory.addItems(InventoryItem.FUEL_ORE, fuelAmt)
                inventory.addItems(InventoryItem.METAL_ORE, metalAmt)
                inventory.addItems(InventoryItem.RARE_METALS, rareAmt)
            } else {
                inventory.addItems(resource, amt)
            }
        }
    }

    companion object {
        const val FUEL_COEFFICIENT = 0.006
        const val FOOD_COEFFICIENT = 0.008 // food per person per turn

        operator fun invoke(name: String, shipClass: ShipClass): Ship {
            val hull = (shipClass.maxHull * Random.range(0.5, 1.0)).toInt()
            val crew = (shipClass.maxCrew * Random.range(0.6, 0.9)).toInt()
            val inv = Inventory(shipClass.cargoCapacity)
            inv.addItems(InventoryItem.FUEL, (inv.capacity/4)+5)
            inv.addItems(InventoryItem.FOOD, inv.capacity/4)
            return Ship(name, shipClass, hull, crew, inv, null, null)
        }
    }
}

data class MiningTarget(val planet: Planet, val resource: InventoryItem)
