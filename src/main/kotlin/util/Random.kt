package util

import kotlin.js.Math

object Random {
    fun range(upper: Int) = (Math.random() * upper).toInt()
    fun range(lower:Int, upper: Int) = lower + range(upper - lower)

    fun <T> choice(list: List<T>): T = list[range(list.size)]
    fun <T> choice(arr: Array<T>): T = arr[range(arr.size)]

    fun normal(): Number = TODO("Compute probit function")
}
