package util

import kotlin.js.Math

class WeightedList<T>(val entries: Map<T, Int>) {
    constructor() : this(emptyMap())
    constructor(vararg pairs: Pair<T, Int>) : this(mapOf(*pairs))

    val size get() = entries.entries.sumBy { it.value }

    fun randomChoice(): T {
        val x = (Math.random() * size).toInt()
        val p = entries.entries
                .scan(0 to (null as T?)) { sum, entry -> (sum.first + entry.value) to entry.key }
                .find { (sum, _) -> x < sum }
        return p?.second ?: throw IllegalArgumentException("List is empty")
    }
}