package game

import serialization.Serializer
import serialization.SerializationModels.SInventory

class Inventory (val capacity: Int, contents: Map<InventoryItem, Int>) {
    constructor(capacity: Int) : this(capacity, emptyMap())

    private val contents: MutableMap<InventoryItem, Int> = contents.toMutableMap()

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

    companion object : Serializer<Inventory, SInventory> {
        override fun serialize(obj: Inventory): SInventory = SInventory(obj.capacity, obj.contents)

        override fun deserialize(serModel: SInventory): Inventory = Inventory(serModel.capacity, serModel.contents)
    }
}
