package util

import kotlin.js.Math

data class IntVector2(val x: Int, val y: Int) {
    operator fun plus(other: IntVector2) = IntVector2(x + other.x, y + other.y)
    operator fun minus(other: IntVector2) = IntVector2(x - other.x, y - other.y)
    operator fun times(scale: Int) = IntVector2(scale * x, scale * y)
    operator fun times(scale: Double) = IntVector2((scale * x).toInt(), (scale * y).toInt())
    operator fun times(other: IntVector2) = IntVector2(x * other.x, y * other.y)

    companion object {
        fun distance(loc1: IntVector2, loc2: IntVector2): Double {
            val dx = (loc1.x - loc2.x).toDouble()
            val dy = (loc1.y - loc2.y).toDouble()
            return Math.sqrt(dx*dx + dy*dy)
        }
    }
}
