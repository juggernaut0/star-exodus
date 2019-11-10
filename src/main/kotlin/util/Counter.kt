package util

class Counter<K>(counts: Map<K, Int> = emptyMap()) {
    private val map: MutableMap<K, Int> = counts.toMutableMap()

    operator fun get(k: K): Int = map[k] ?: 0
    operator fun set(k: K, v: Int) {
        if (v == 0) {
            map.remove(k)
        } else {
            map[k] = v
        }
    }

    fun asMap(): Map<K, Int> {
        return map
    }
}
