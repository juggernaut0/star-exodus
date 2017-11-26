package game

import serialization.Serializable

class Inventory (val capacity: Int, contents: Map<InventoryItem, Int>) : Serializable {
    constructor(capacity: Int) : this(capacity, emptyMap())

    private val contents: MutableMap<InventoryItem, Int> = contents.toMutableMap()
    val items: List<Pair<InventoryItem, Int>> get() = contents.map { (k, v) -> k to v }

    val usedSpace get() = contents.values.sum()
    val freeSpace get() = capacity - usedSpace

    operator fun get(item: InventoryItem): Int = contents[item] ?: 0

    /**
     * @return Amount actually added
     */
    fun addItems(item: InventoryItem, amount: Int = 1): Int {
        val trueAmt = minOf(freeSpace, amount)
        contents[item] = trueAmt + this[item]
        return trueAmt
    }

    fun removeItems(item: InventoryItem, amount: Int = 1): Int {
        val trueAmt = minOf(this[item], amount)
        val newAmt = this[item] - trueAmt
        if (newAmt == 0) {
            contents.remove(item)
        } else {
            contents[item] = newAmt
        }
        return trueAmt
    }

    fun asMap(): Map<InventoryItem, Int> = contents
}
