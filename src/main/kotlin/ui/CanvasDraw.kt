package ui

import org.w3c.dom.CanvasRenderingContext2D
import kotlin.js.Math

class CanvasDraw(private val ctx: CanvasRenderingContext2D) {
    fun circle(center: Point, radius: Double, lineStyle: LineStyle = LineStyle(Color.BLACK), fillColor: Color = Color.TRANSPARENT) {
        ctx.strokeStyle = lineStyle.color.toCSS()
        ctx.lineWidth = lineStyle.width
        ctx.fillStyle = fillColor.toCSS()

        ctx.beginPath()
        ctx.arc(center.x, center.y, radius, 0.0, 2 * Math.PI)
        ctx.fill()
        ctx.stroke()
    }

    fun clear() {
        ctx.clearRect(0.0, 0.0, ctx.canvas.width.toDouble(), ctx.canvas.height.toDouble())
    }

    fun clear(color: Color) {
        ctx.fillStyle = color.toCSS()
        ctx.fillRect(0.0, 0.0, ctx.canvas.width.toDouble(), ctx.canvas.height.toDouble())
    }
}

data class Point(val x: Double, val y: Double) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
}

data class LineStyle(val color: Color, val width: Double = 1.0)

data class Color(val r: Int, val g: Int, val b: Int, val a: Double = 1.0) {
    companion object {
        val TRANSPARENT = Color(0, 0, 0, 0.0)
        val BLACK = Color(0, 0, 0)
        val RED = Color(255, 0, 0)
        val GREEN = Color(0, 255, 0)
        val BLUE = Color(0, 0, 255)
        val WHILE = Color(255, 255, 255)
    }

    fun withAlpha(a: Double) = Color(r, g, b, a)

    fun toCSS(): String = "rgba($r, $g, $b, $a)"
}
