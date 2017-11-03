package game

import serialization.DeserializationException
import serialization.Serializer
import serialization.SShip
import util.Random

class Ship private constructor(val name: String, val shipClass: ShipClass, hullPoints: Int, crew: Int, val inventory: Inventory) {
    constructor(name: String, shipClass: ShipClass)
            : this(name, shipClass,
            (shipClass.maxHull * Random.range(0.5, 1.0)).toInt(),
            (shipClass.maxCrew * Random.range(0.6, 0.9)).toInt(),
            Inventory(shipClass.cargoCapacity))

    var hullPoints: Int = hullPoints
        private set
    var crew: Int = crew
        private set

    val mass get() = shipClass.maxCrew/2 + inventory.freeSpace/2 + inventory.usedSpace + shipClass.hanger*2
    val destroyed get() = hullPoints == 0

    fun modHullPoints(amt: Int): Int {
        hullPoints = minOf(maxOf(hullPoints + amt, 0), shipClass.maxHull)
        return hullPoints
    }

    companion object : Serializer<Ship, SShip> {
        override fun serialize(obj: Ship): SShip =
                SShip(obj.name, obj.shipClass.name, obj.hullPoints, obj.crew, Inventory.serialize(obj.inventory))

        override fun deserialize(serModel: SShip): Ship {
            val inv = Inventory.deserialize(serModel.inventory)
            val shipClass: ShipClass = ShipClass[serModel.shipClass] ?: throw DeserializationException("shipClass ${serModel.shipClass} not found")
            return Ship(serModel.name, shipClass, serModel.hullPoints, serModel.crew, inv)
        }
    }
}
