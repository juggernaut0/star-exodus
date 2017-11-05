package ui

import PIXI.SystemRenderer
import angular.HttpService
import angular.Scope
import game.ExodusGame
import org.w3c.dom.HTMLElement
import serialization.Base64
import kotlin.browser.document
import kotlin.browser.window

import serialization.JsonSerializer.toJson

@Suppress("MemberVisibilityCanPrivate")
class StarExodusController(val scope: Scope, http: HttpService) {
    private lateinit var game: ExodusGame
    private val renderer: SystemRenderer

    var clickedStarName: String? = null
    var fleet: Array<ShipView> = emptyArray()

    init {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({ game = ExodusGame(loader); refreshMap() })

        renderer = initPixi("mapPanel", 800, 800)
    }

    private fun initPixi(canvasId: String, width: Int, height: Int): SystemRenderer {
        val renderer = PIXI.autoDetectRenderer(width, height)
        val gamePanel = (document.getElementById(canvasId) ?: throw Exception("Can't find game panel")) as HTMLElement
        gamePanel.appendChild(renderer.view)
        renderer.view.style.display = "block"
        renderer.autoResize = true
        renderer.resize(width, height)
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

    @JsName("refreshFleet")
    fun refreshFleet() {
        fleet = game.fleet.ships.map { ShipView(it) }.toTypedArray()
    }

    @JsName("saveGame")
    fun saveGame() {
        window.localStorage.setItem("game", Base64.encode(byteArrayOf()))
    }

    fun jsonify() {
        val url = "data:application/json," + encodeURIComponent(ExodusGame.serialize(game).toJson())
        console.log(url)
        window.open(url, "_blank")
    }
}

external fun encodeURIComponent(string: String): String
