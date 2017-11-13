package ui

import PIXI.SystemRenderer
import angular.HttpService
import angular.Scope
import game.ExodusGame
import org.w3c.dom.HTMLElement
import serialization.JsonSerializer
import kotlin.browser.document
import kotlin.browser.window

@Suppress("MemberVisibilityCanPrivate", "unused")
class StarExodusController(val scope: Scope, http: HttpService) {
    private lateinit var game: ExodusGame
    private val renderer: SystemRenderer
    private var saveCleared = false

    var clickedStarName: String? = null
    var fleet: Array<ShipView> = emptyArray()

    init {


        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                ExodusGame.deserialize(JsonSerializer.loadGame(savedString), loader)
            } else {
                ExodusGame(loader)
            }
            refreshMap()
        })


        renderer = initPixi("mapPanel", 800, 800)

        window.onbeforeunload = { if (!saveCleared) saveGame(); null }
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
        window.localStorage.setItem("savedgame", JsonSerializer.saveGame(ExodusGame.serialize(game)))
    }

    @JsName("clearSave")
    fun clearSave() {
        window.localStorage.removeItem("savedgame")
        saveCleared = true
    }
}
