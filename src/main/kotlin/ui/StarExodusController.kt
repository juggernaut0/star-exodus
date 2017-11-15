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
    var totalPopulation: Int = 0
    var shipDetails: ShipView? = null

    init {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                ExodusGame.deserialize(JsonSerializer.loadGame(savedString))
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

        game.galaxy.stars.map {
            Shapes.circle(it.location.toPoint(), 2.0, lineStyle = LineStyle(Color.TRANSPARENT), fillColor = Color.WHITE) { _ ->
                clickedStarName = it.name
                scope.apply()
            }
        }.forEach { stage.addChild(it) }

        stage.addChild(Shapes.circle(game.fleet.location.toPoint(), 4.0, lineStyle = LineStyle(Color.RED, 2.0)))

        renderer.render(stage)
    }

    @JsName("refreshFleet")
    fun refreshFleet() {
        fleet = game.fleet.ships.map { ShipView(it) }.toTypedArray()
        totalPopulation = game.fleet.ships.sumBy { it.crew }
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

    @JsName("openShipCollapse")
    fun openShipCollapse(shipView: ShipView) {
        shipDetails = shipView
        js("$('#shipDetails')").collapse("show")
    }

    @JsName("closeShipCollapse")
    fun closeShipCollapse() {
        shipDetails = null
        js("$('#shipDetails')").collapse("hide")
    }

    @JsName("activeClass")
    fun activeClass(shipView: ShipView) = if (shipView == shipDetails) "table-primary" else ""

    private fun util.Location.toPoint(): Point =
            Point(x * renderer.width.toInt() / game.galaxy.mapSize, y * renderer.height.toInt() / game.galaxy.mapSize)
}
