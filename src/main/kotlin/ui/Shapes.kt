package ui

import PIXI.interaction.InteractionEvent

object Shapes {
    fun circle(center: Point, radius: Double, lineStyle: LineStyle = LineStyle(Color.BLACK), fillColor: Color = Color.TRANSPARENT, onClick: ((InteractionEvent) -> Unit)? = null): PIXI.Graphics {
        val gr = PIXI.Graphics()
                .lineStyle(lineStyle.width, lineStyle.color.toHex(), lineStyle.color.a)
                .beginFill(fillColor.toHex(), fillColor.a)
                .drawCircle(center.x, center.y, radius)

        if (onClick != null) {
            gr.interactive = true
            gr.on("mousedown", onClick)
        }

        return gr
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
        val WHITE = Color(255, 255, 255)
    }

    fun withAlpha(a: Double) = Color(r, g, b, a)

    fun toCSS(): String = "rgba($r, $g, $b, $a)"

    fun toHex(): Int = (r shl 16) or (g shl 8) or b
}
