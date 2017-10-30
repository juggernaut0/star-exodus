@file:JsQualifier("PIXI")
package PIXI

import PIXI.interaction.InteractiveTarget

external open class DisplayObject : InteractiveTarget {
    open fun updateTransform(): Unit
    open fun renderWebGL(renderer: WebGLRenderer): Unit
    open fun renderCanvas(renderer: CanvasRenderer): Unit

    var x: Number
    var y: Number

    // TODO
}