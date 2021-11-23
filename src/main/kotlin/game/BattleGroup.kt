package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer

class BattleGroup internal constructor(val name: String, ships: List<Ship>, val isEnemy: Boolean = false) {
    constructor(name: String) : this(name, emptyList())

    private val _ships = ships.toMutableList()
    val ships: List<Ship> get() = _ships
    val speed: Int get() = _ships.maxBy { it.speed }?.speed ?: 0

    fun transferShip(ship: Ship, target: BattleGroup) {
        _ships.remove(ship)
        target._ships.add(ship)
    }

    fun abandonShip(ship: Ship) {
        check(ship in ships) { "ship must be in this group" }
        _ships.remove(ship)
    }

    internal fun cleanupDestroyed() {
        _ships.removeAll { it.destroyed }
    }

    object Serial : Serializer<BattleGroup, Serial.Data> {
        @Serializable
        class Data(
                val name: String,
                val ships: List<Ship.Serial.Data>,
                val enemy: Boolean
        )

        override fun save(model: BattleGroup, refs: RefSaver): Data {
            return Data(model.name, model._ships.map { Ship.Serial.save(it, refs) }, model.isEnemy)
        }

        override fun load(data: Data, refs: RefLoader): BattleGroup {
            return BattleGroup(data.name, data.ships.map { Ship.Serial.load(it, refs) }, data.enemy)
        }
    }
}