@file:JsQualifier("PIXI")
package PIXI

external open class Rectangle(x: Number, y: Number, width: Number, height: Number) : HitArea {
    open var x: Number = definedExternally
    open var y: Number = definedExternally
    open var width: Number = definedExternally
    open var height: Number = definedExternally
    open var type: Number = definedExternally
    open var left: Number = definedExternally
    open var right: Number = definedExternally
    open var top: Number = definedExternally
    open var bottom: Number = definedExternally
    open fun clone(): Rectangle = definedExternally
    open fun copy(rectangle: Rectangle): Rectangle = definedExternally
    override fun contains(x: Number, y: Number): Boolean = definedExternally
    open fun pad(paddingX: Number, paddingY: Number): Unit = definedExternally
    open fun fit(rectangle: Rectangle): Unit = definedExternally
    open fun enlarge(rectangle: Rectangle): Unit = definedExternally
    companion object {
        var EMPTY: Rectangle = definedExternally
    }
}