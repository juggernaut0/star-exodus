package game

import serialization.Serializable
import util.Event
import util.EventEmitter
import util.Random
import kotlin.math.*

class Ship(
        name: String,
        val shipClass: ShipClass,
        hullPoints: Int,
        crew: Int,
        val inventory: Inventory,
        var exploring: Planet?,
        var mining: MiningTarget?
) : EventEmitter<Ship>(), Serializable {
    var name: String = name
        private set

    var hullPoints: Int = hullPoints
        private set
    inline val maxHull get() = shipClass.maxHull

    var crew: Int = crew
        private set
    inline val minCrew get() = shipClass.minCrew
    inline val maxCrew get() = shipClass.maxCrew

    val mass get() = maxCrew/2 + inventory.freeSpace/2 + inventory.usedSpace + shipClass.hanger*2

    // fuel per distance unit per turn
    val fuelConsumption get() = sqrt(mass.toDouble()) * FUEL_COEFFICIENT
    // food per turn
    val foodConsumption get() = ceil(crew * FOOD_COEFFICIENT).toInt()

    val explorers get() = min(floor(0.1 * crew).toInt(), 50)

    val destroyed get() = hullPoints == 0

    val onMine = Event<Ship, MiningEventArgs>().bind(this)

    fun rename(newName: String) {
        if (newName.isNotBlank()){
            name = newName
        }
    }

    // returns amount actually changed
    fun modHullPoints(amt: Int): Int {
        val oldHp = hullPoints
        hullPoints = minOf(maxOf(hullPoints + amt, 0), maxHull)
        return hullPoints - oldHp
    }

    fun modCrew(amt: Int): Int {
        val oldCrew = crew
        crew = minOf(maxOf(crew + amt, 0), maxCrew)
        return crew - oldCrew
    }

    fun transferCrew(dest: Ship, amt: Int): Int {
        val actual = arrayOf(crew, dest.maxCrew - dest.crew, max(amt, 0)).min() ?: 0
        modCrew(-actual)
        dest.modCrew(actual)
        return actual
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
            fun mineAndInvoke(item: InventoryItem, amount: Int) {
                val actual = inventory.addItems(item, amount)
                if (actual > 0) {
                    onMine(MiningEventArgs(planet, item, actual))
                }
            }

            val amt = miningYield(this)

            if (resource == InventoryItem.FUEL_ORE || resource == InventoryItem.METAL_ORE) {
                val fuelAmt = Random.range(amt / 2)
                val rareAmt = if (planet.discoveredFeatures.contains(PlanetFeature.RARE_ELEMENTS)) Random.range((amt - fuelAmt) / 2) else 0
                val metalAmt = amt - fuelAmt - rareAmt
                mineAndInvoke(InventoryItem.FUEL_ORE, fuelAmt)
                mineAndInvoke(InventoryItem.METAL_ORE, metalAmt)
                mineAndInvoke(InventoryItem.RARE_METALS, rareAmt)
            } else {
                mineAndInvoke(resource, amt)
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

    data class MiningTarget(val planet: Planet, val resource: InventoryItem)
    data class MiningEventArgs(val planet: Planet, val resource: InventoryItem, val amount: Int)
}
