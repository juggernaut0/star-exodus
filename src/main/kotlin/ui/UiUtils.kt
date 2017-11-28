package ui

import PIXI.SystemRenderer
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
