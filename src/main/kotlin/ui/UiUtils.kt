package ui

import PIXI.SystemRenderer
import game.Ship
import kui.AbstractMarkupBuilder
import kui.Component
import org.w3c.dom.HTMLElement
import kotlin.browser.document

fun initPixi(canvasId: String, width: Int, height: Int): SystemRenderer {
    val renderer = PIXI.autoDetectRenderer(width, height)
    val gamePanel = (document.getElementById(canvasId) ?: throw Exception("Can't find game panel")) as HTMLElement
    gamePanel.appendChild(renderer.view)
    renderer.view.style.display = "block"
    renderer.autoResize = true
    renderer.resize(width, height)
    return renderer
}

// Bootstrap button
fun bsBtnBlock(type: String) = listOf("btn", "btn-$type", "btn-block")
fun bsBtnOutline(type: String) = listOf("btn", "btn-outline-$type")

// Bootstrap toggles
fun bsModalToggle(target: String) = mapOf("data-toggle" to "modal", "data-target" to "#$target")
fun bsCollapseToggle(target: String, parent: String): Map<String, String> {
    return mapOf("data-toggle" to "collapse", "data-target" to "#$target", "data-parent" to "#$parent")
}

const val CLOSE = "\u00d7"

fun Ship.toView(): ShipView = ShipView(this)
