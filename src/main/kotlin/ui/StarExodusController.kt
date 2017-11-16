package ui

import PIXI.SystemRenderer
import angular.HttpService
import angular.Scope
import game.ExodusGame
import jQuery
import org.w3c.dom.HTMLElement
import serialization.JsonSerializer
import util.MutVector2
import kotlin.browser.document
import kotlin.browser.window

@Suppress("MemberVisibilityCanPrivate", "unused")
class StarExodusController(val scope: Scope, http: HttpService) {
    private lateinit var game: ExodusGame
    private val galaxyRenderer: SystemRenderer
    private val systemRenderer: SystemRenderer
    private var saveCleared = false

    var clickedStar: StarView? = null
    var fleet: Array<ShipView> = emptyArray()
    var totalPopulation: Int = 0
    var shipDetails: ShipView? = null
    var currentSystem: StarView? = null
    var selectedDestination = MutVector2()

    init {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                ExodusGame.deserialize(JsonSerializer.loadGame(savedString))
            } else {
                ExodusGame(loader)
            }
        })

        galaxyRenderer = initPixi("mapPanel", 800, 800)
        systemRenderer = initPixi("systemMap", 400, 400)

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

        game.galaxy.stars.asSequence().map {
            Shapes.circle(it.location.toPoint(), 2.0, lineStyle = LineStyle(Color.TRANSPARENT), fillColor = Color.WHITE) { _ ->
                clickedStar = StarView(it)
                scope.apply()
            }
        }.forEach { stage.addChild(it) }

        stage.addChild(Shapes.circle(game.fleet.location.toPoint(), 4.0, lineStyle = LineStyle(Color.RED, 2.0)))

        galaxyRenderer.render(stage)
    }

    @JsName("refreshFleet")
    fun refreshFleet() {
        fleet = game.fleet.ships.map { ShipView(it) }.toTypedArray()
        totalPopulation = game.fleet.ships.sumBy { it.crew }
    }

    @JsName("refreshStar")
    fun refreshStar() {
        currentSystem = game.galaxy.getStarAt(game.fleet.location)?.let { StarView(it) }
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
        jQuery("#shipDetails").collapse("show")
    }

    @JsName("closeShipCollapse")
    fun closeShipCollapse() {
        shipDetails = null
        jQuery("#shipDetails").collapse("hide")
    }

    @JsName("activeClass")
    fun activeClass(shipView: ShipView) = if (shipView == shipDetails) "table-primary" else ""

    @JsName("setDestination")
    fun setDestination() {
        game.fleet.destination = selectedDestination.toIntVector()
    }

    @JsName("nextDay")
    fun nextDay() {
        game.nextDay()
    }

    private fun util.IntVector2.toPoint(): Point =
            Point(x * galaxyRenderer.width.toInt() / game.galaxy.mapSize, y * galaxyRenderer.height.toInt() / game.galaxy.mapSize)
}
