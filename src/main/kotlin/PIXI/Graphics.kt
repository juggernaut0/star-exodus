@file:JsQualifier("PIXI")
package PIXI

external class Graphics : Container {
    fun beginFill(color: Number, alpha: Number = definedExternally): Graphics
    fun endFill(): Graphics

    fun lineStyle(lineWidth: Number, color: Number = definedExternally, alpha: Number = definedExternally): Graphics

    fun moveTo(x: Number, y: Number): Graphics
    fun lineTo(x: Number, y: Number): Graphics

    fun drawCircle(x: Number, y: Number, radius: Number): Graphics

    // TODO
}