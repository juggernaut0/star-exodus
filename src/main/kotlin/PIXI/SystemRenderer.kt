@file:JsQualifier("PIXI")
package PIXI

import org.w3c.dom.HTMLCanvasElement

external open class SystemRenderer {
    constructor(system: String, options: RendererOptions? = definedExternally /* null */)
    constructor(system: String, screenWidth: Number? = definedExternally /* null */, screenHeight: Number? = definedExternally /* null */, options: RendererOptions? = definedExternally /* null */)
    open var type: Number
    open var options: RendererOptions
    open var screen: Rectangle
    open var width: Number
    open var height: Number
    open var view: HTMLCanvasElement
    open var resolution: Number
    open var transparent: Boolean
    open var autoResize: Boolean
    open var blendModes: Any
    open var preserveDrawingBuffer: Boolean
    open var clearBeforeRender: Boolean
    open var roundPixels: Boolean
    open var _backgroundColor: Number
    open var _backgroundColorRgba: Array<Number>
    open var _backgroundColorString: String
    open var _tempDisplayObjectParent: Container
    open var _lastObjectRendered: DisplayObject
    open fun resize(screenWidth: Number, screenHeight: Number): Unit
    open fun generateTexture(displayObject: DisplayObject, scaleMode: Number? = definedExternally /* null */, resolution: Number? = definedExternally /* null */): RenderTexture
    open fun render(displayObject: DisplayObject, renderTexture: RenderTexture? = definedExternally, clear: Boolean? = definedExternally, transform: Transform? = definedExternally, skipUpdateTransform: Boolean? = definedExternally)
    open fun destroy(removeView: Boolean? = definedExternally /* null */): Unit
}