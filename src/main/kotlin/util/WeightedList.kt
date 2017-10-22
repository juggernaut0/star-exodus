package util

import kotlin.js.Math

class WeightedList<T>(private val entries: Map<T, Int>) {
    constructor() : this(emptyMap())
    constructor(vararg pairs: Pair<T, Int>) : this(mapOf(*pairs))

    val size get() = entries.entries.sumBy { it.value }

    operator fun get(i: Int): T {
        if (i < 0)
            throw IndexOutOfBoundsException()

        val p = entries.entries
                .scan(0 to (null as T?)) { sum, entry -> (sum.first + entry.value) to entry.key }
                .find { (sum, _) -> i < sum } ?: throw IndexOutOfBoundsException()
        return p.second!!
    }

    fun randomChoice(): T {
        val x = (Math.random() * size).toInt()
        return get(x)
    }
}