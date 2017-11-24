package game

import serialization.Serializer
import serialization.SerializationModels.SShip
import util.Random
import kotlin.js.Math

class Ship private constructor(
        name: String,
        val shipClass: ShipClass,
        hullPoints: Int,
        crew: Int,
        val inventory: Inventory,
        var exploring: Int?
) {
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

    fun eatFood() {
        val curFood = inventory[InventoryItem.FOOD]
        if (curFood >= foodConsumption) {
            inventory.removeItems(InventoryItem.FOOD, foodConsumption)
        } else {
            val toKill = crew - (curFood / FOOD_COEFFICIENT).toInt()
            modCrew(-toKill)
            inventory.removeItems(InventoryItem.FOOD, curFood)
        }
    }

    companion object : Serializer<Ship, SShip> {
        const val FUEL_COEFFICIENT = 0.006
        const val FOOD_COEFFICIENT = 0.008 // food per person per turn

        override fun serialize(obj: Ship): SShip =
                SShip(obj.name, obj.shipClass, obj.hullPoints, obj.crew, Inventory.serialize(obj.inventory), obj.exploring)

        override fun deserialize(serModel: SShip): Ship =
                Ship(serModel.name, serModel.shipClass, serModel.hullPoints, serModel.crew, Inventory.deserialize(serModel.inventory), serModel.exploring)

        operator fun invoke(name: String, shipClass: ShipClass): Ship {
            val hull = (shipClass.maxHull * Random.range(0.5, 1.0)).toInt()
            val crew = (shipClass.maxCrew * Random.range(0.6, 0.9)).toInt()
            val inv = Inventory(shipClass.cargoCapacity)
            inv.addItems(InventoryItem.FUEL, (inv.capacity/4)+5)
            inv.addItems(InventoryItem.FOOD, inv.capacity/4)
            return Ship(name, shipClass, hull, crew, inv, null)
        }
    }
}
