package util

import kotlin.js.Math

data class Location(val x: Int, val y: Int) {
    companion object {
        fun distance(loc1: Location, loc2: Location): Double {
            val dx = (loc1.x - loc2.x).toDouble()
            val dy = (loc1.y - loc2.y).toDouble()
            return Math.sqrt(dx*dx + dy*dy)
        }
    }
}
