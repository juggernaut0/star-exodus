package util

class WeightedList<T>(private val entries: List<Pair<T, Int>>) : AbstractList<T>() {
    constructor(vararg pairs: Pair<T, Int>) : this(listOf(*pairs))
    constructor(map: Map<T, Int>) : this(map.entries.map { it.key to it.value })

    override val size get() = entries.sumBy { it.second }

    override operator fun get(index: Int): T {
        if (index < 0)
            throw IndexOutOfBoundsException()

        val p = entries
                .scan(0 to (null as T?)) { sum, entry -> (sum.first + entry.second) to entry.first }
                .find { (sum, _) -> index < sum } ?: throw IndexOutOfBoundsException()
        return p.second!!
    }
}
