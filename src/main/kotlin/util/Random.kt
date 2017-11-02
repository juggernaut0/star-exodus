package util

import kotlin.js.Math

object Random {
    fun range(upper: Int) = (Math.random() * upper).toInt()
    fun range(upper: Double) = Math.random() * upper
    fun range(lower: Int, upper: Int) = lower + range(upper - lower)
    fun range(lower: Double, upper: Double) = lower + range(upper - lower)

    fun <T> choice(list: List<T>): T = list[range(list.size)]
    fun <T> choice(arr: Array<T>): T = arr[range(arr.size)]

    fun <T> sample(list: List<T>, count: Int): Set<T> {
        val size = list.size
        if (count > size) throw IllegalArgumentException("count is greater than size of list")

        val set: HashSet<T>
        if (count < size) {
            set = HashSet()
            while (set.size < count) {
                set.add(choice(list))
            }
        } else {
            set = HashSet(list)
            while (set.size > count) {
                set.remove(choice(list))
            }
        }

        return set
    }

    //private val NORMAL_MAGIC = 4 * Math.exp(-0.5)/Math.sqrt(2.0)
    private const val NORMAL_MAGIC = 4 * 0.6065306597126334 / 1.4142135623730951
    fun normal(mu: Double = 1.0, sigma: Double = 1.0): Number {
        var z: Double
        while (true) {
            val u1 = Math.random()
            val u2 = 1 - Math.random()
            z = NORMAL_MAGIC*(u1-0.5)/u2
            if (z*z/4.0 <= -Math.log(u2)) {
                break
            }
        }
        return mu + sigma*z
    }
}
