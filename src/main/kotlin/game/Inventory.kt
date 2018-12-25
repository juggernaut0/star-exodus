package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer

class Inventory (val capacity: Int, contents: Map<InventoryItem, Int>) {
    constructor(capacity: Int) : this(capacity, emptyMap())

    private val contents: MutableMap<InventoryItem, Int> = contents.filterTo(mutableMapOf()) { it.value > 0 }
    val items: List<Pair<InventoryItem, Int>> get() = contents.map { (k, v) -> k to v }

    val usedSpace get() = contents.values.sum()
    val freeSpace get() = capacity - usedSpace

    operator fun get(item: InventoryItem): Int = contents[item] ?: 0

    /**
     * @return Amount actually added
     */
    fun addItems(item: InventoryItem, amount: Int = 1): Int {
        val trueAmt = minOf(freeSpace, amount.coerceAtLeast(0))
        if (trueAmt > 0) {
            contents[item] = trueAmt + this[item]
        }
        return trueAmt
    }

    fun removeItems(item: InventoryItem, amount: Int = 1): Int {
        val trueAmt = minOf(this[item], amount.coerceAtLeast(0))
        val newAmt = this[item] - trueAmt
        if (newAmt == 0) {
            contents.remove(item)
        } else {
            contents[item] = newAmt
        }
        return trueAmt
    }

    fun transferItemsTo(other: Inventory, item: InventoryItem, amount: Int): Int {
        val actual = arrayOf(this[item], other.freeSpace, amount.coerceAtLeast(0)).min() ?: 0
        if (actual > 0) {
            removeItems(item, actual)
            other.addItems(item, actual)
        }
        return actual
    }

    fun asMap(): Map<InventoryItem, Int> = contents

    object Serial : Serializer<Inventory, Serial.Data> {
        @Serializable
        class Data(val capacity: Int, val contents: Map<String, Int>)

        override fun save(model: Inventory, refs: RefSaver): Data {
            return Data(model.capacity, model.contents.mapKeys { it.key.name })
        }

        override fun load(data: Data, refs: RefLoader): Inventory {
            return Inventory(data.capacity, data.contents.mapKeys { InventoryItem.valueOf(it.key) })
        }
    }
}
