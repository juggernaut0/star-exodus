package PIXI

external open class PointLike {
    var x: Number
    var y: Number

    fun set(x: Number, y: Number)
}

external class Point(x: Number, y: Number) : PointLike