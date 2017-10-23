package util

class WeightedList<T>(private val entries: Map<T, Int>) : AbstractList<T>() {
    constructor() : this(emptyMap())
    constructor(vararg pairs: Pair<T, Int>) : this(mapOf(*pairs))

    override val size get() = entries.entries.sumBy { it.value }

    override operator fun get(index: Int): T {
        if (index < 0)
            throw IndexOutOfBoundsException()

        val p = entries.entries
                .scan(0 to (null as T?)) { sum, entry -> (sum.first + entry.value) to entry.key }
                .find { (sum, _) -> index < sum } ?: throw IndexOutOfBoundsException()
        return p.second!!
    }
}