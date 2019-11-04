package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer

class BattleGroup internal constructor(val name: String, ships: List<Ship>) {
    constructor(name: String) : this(name, emptyList())

    private val _ships = ships.toMutableList()
    val ships: List<Ship> get() = _ships

    fun transferShip(ship: Ship, target: BattleGroup) {
        _ships.remove(ship)
        target._ships.add(ship)
    }

    fun abandonShip(ship: Ship) {
        check(ship in ships) { "ship must be in this group" }
        _ships.remove(ship)
    }

    object Serial : Serializer<BattleGroup, Serial.Data> {
        @Serializable
        class Data(
                val name: String,
                val ships: List<Int>
        )

        override fun save(model: BattleGroup, refs: RefSaver): Data {
            return Data(model.name, model._ships.map { refs.saveShipRef(it) })
        }

        override fun load(data: Data, refs: RefLoader): BattleGroup {
            return BattleGroup(data.name, data.ships.map { refs.loadShipRef(it) })
        }
    }
}