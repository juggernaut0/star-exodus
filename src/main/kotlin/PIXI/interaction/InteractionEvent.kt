package PIXI.interaction

import PIXI.DisplayObject

external interface InteractionEvent {
    var stopped: Boolean
    var target: DisplayObject
    var currentTarget: DisplayObject
    var type: String
    var data: InteractionData
    fun stopPropagation()
}
