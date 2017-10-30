package PIXI.interaction

import PIXI.DisplayObject
import PIXI.Point

external class InteractionData {
    val global: Point
    val target: DisplayObject
    //originalEvent: MouseEvent | TouchEvent | PointerEvent; TODO
    val identifier: Number
    val isPrimary: Boolean
    val button: Number
    val buttons: Number
    val width: Number
    val height: Number
    val tiltX: Number
    val tiltY: Number
    val pointerType: String
    val pressure: Number
    val rotationAngle: Number
    val twist: Number
    val tangentialPressure: Number
    val pointerID: Number
    fun getLocalPosition(displayObject: DisplayObject, point: Point = definedExternally, globalPos: Point = definedExternally): Point
}