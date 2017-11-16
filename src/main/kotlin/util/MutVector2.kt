package util

data class MutVector2(var x: Double = 0.0, var y: Double = 0.0) {
    fun toIntVector() = IntVector2(x.toInt(), y.toInt())
}
