package ui

import PIXI.SystemRenderer
import angular.HttpService
import angular.Scope
import game.ExodusGame
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import kotlin.browser.document

@Suppress("MemberVisibilityCanPrivate")
class StarExodusController(val scope: Scope, http: HttpService) {
    private lateinit var game: ExodusGame
    private val renderer: SystemRenderer

    var clickedStarName: String? = null

    init {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({ game = ExodusGame(loader); refreshMap() })

        renderer = initPixi("mapPanel")
    }

    private fun initPixi(canvasId: String): SystemRenderer {
        val renderer = PIXI.autoDetectRenderer(256,256)
        val gamePanel = (document.getElementById(canvasId) ?: throw Exception("Can't find game panel")) as HTMLElement
        gamePanel.appendChild(renderer.view)
        renderer.view.style.display = "block"
        renderer.autoResize = true
        renderer.resize(gamePanel.offsetWidth, gamePanel.offsetHeight)
        return renderer
    }

    @JsName("refreshMap")
    fun refreshMap() {
        val stage = PIXI.Container()
        for (star in game.galaxy.stars) {
            val x = star.location.x * renderer.width.toInt() / game.galaxy.mapSize
            val y = star.location.y * renderer.height.toInt() / game.galaxy.mapSize
            val sprite = Shapes.circle(Point(x, y), 2.0, lineStyle = LineStyle(Color.TRANSPARENT), fillColor = Color.WHITE) {
                clickedStarName = star.name
                scope.apply()
            }
            stage.addChild(sprite)
        }
        renderer.render(stage)
    }
}
