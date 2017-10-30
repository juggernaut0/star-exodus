@file:JsQualifier("PIXI")
package PIXI

external open class Container : DisplayObject {
    open fun getChildByName(name: String): DisplayObject
    open var children: Array<DisplayObject>
    open var width: Number
    open var height: Number
    open var onChildrenChange: (args: Any) -> Unit
    open fun <T : DisplayObject> addChild(child: T, vararg additionalChildren: DisplayObject): T
    open fun <T : DisplayObject> addChildAt(child: T, index: Number): T
    open fun swapChildren(child: DisplayObject, child2: DisplayObject): Unit
    open fun getChildIndex(child: DisplayObject): Number
    open fun setChildIndex(child: DisplayObject, index: Number): Unit
    open fun getChildAt(index: Number): DisplayObject
    open fun removeChild(child: DisplayObject): DisplayObject
    open fun removeChildAt(index: Number): DisplayObject
    open fun removeChildren(beginIndex: Number? = definedExternally /* null */, endIndex: Number? = definedExternally /* null */): Array<DisplayObject>
    override fun updateTransform(): Unit
    open fun calculateBounds(): Unit
    open fun _calculateBounds(): Unit
    open fun containerUpdateTransform(): Unit
    override fun renderWebGL(renderer: WebGLRenderer): Unit
    open fun renderAdvancedWebGL(renderer: WebGLRenderer): Unit
    open fun _renderWebGL(renderer: WebGLRenderer): Unit
    open fun _renderCanvas(renderer: CanvasRenderer): Unit
    override fun renderCanvas(renderer: CanvasRenderer): Unit
    open fun destroy(options: DestroyOptions? = definedExternally /* null */): Unit
    open fun destroy(options: Boolean? = definedExternally /* null */): Unit
}