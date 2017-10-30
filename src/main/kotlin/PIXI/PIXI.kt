@file:JsModule("PIXI")
@file:JsNonModule
package PIXI

import PIXI.utils.EventEmitter

external fun autoDetectRenderer(width: Int, height: Int, options: PIXI.RendererOptions? = definedExternally, forceCanvas: Boolean? = definedExternally): SystemRenderer

// TODO all below
external open class RendererOptions

external open class Texture : EventEmitter<Texture>
external class RenderTexture : Texture

external class Transform
