@file:JsQualifier("PIXI.interaction")
package PIXI.interaction

import PIXI.utils.EventEmitter

external open class InteractiveTarget : EventEmitter<InteractionEvent> {
    var interactive: Boolean
    var interactiveChildren: Boolean
    var hitArea: PIXI.HitArea
    var buttonMode: Boolean
    var cursor: String
}