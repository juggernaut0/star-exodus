package util

import kotlin.math.ln

object Random {
    private val rand = kotlin.random.Random.Default

    fun chance(p: Double): Boolean = rand.nextDouble() < p
    fun chances(p: Double, times: Int): Int {
        var r = 0
        repeat(times) { if (chance(p)) r += 1 }
        return r
    }

    fun range(upper: Int) = rand.nextInt(upper)
    fun range(upper: Double) = rand.nextDouble(upper)
    fun range(lower: Int, upper: Int) = rand.nextInt(lower, upper)
    fun range(lower: Double, upper: Double) = rand.nextDouble(lower, upper)

    fun <T> choice(list: List<T>): T = list[range(list.size)]
    fun <T> choice(arr: Array<T>): T = arr[range(arr.size)]

    fun <T> sample(list: List<T>, count: Int): Set<T> {
        val size = list.size
        require(count <= size) { "count is greater than size of list" }

        val set: HashSet<T>
        if (count < size / 2) {
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
    fun normal(mu: Double = 0.0, sigma: Double = 1.0): Double {
        var z: Double
        while (true) {
            val u1 = rand.nextDouble()
            val u2 = 1 - rand.nextDouble()
            z = NORMAL_MAGIC*(u1-0.5)/u2
            if (z*z/4.0 <= -ln(u2)) {
                break
            }
        }
        return mu + sigma*z
    }
}
