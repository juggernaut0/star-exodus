package game

class Inventory(val capacity: Int) {
    private val contents: MutableMap<InventoryItem, Int> = mutableMapOf()

    val usedSpace get() = contents.values.sum()
    val freeSpace get() = capacity - usedSpace

    operator fun get(item: InventoryItem): Int = contents.getOrDefault(item, 0)

    /**
     * @return Amount actually added
     */
    fun addItems(item: InventoryItem, amount: Int = 1): Int {
        val trueAmt = maxOf(freeSpace, amount)
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
}