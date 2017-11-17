package ui

import PIXI.SystemRenderer
import angular.HttpService
import angular.Scope
import game.ExodusGame
import game.PlanetType.*
import game.StarType.*
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
    var fleetSpeed: Int = 0
    var selectedShip: ShipView? = null
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
        systemRenderer = initPixi("systemMap", 400, 200)

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
        closeShipCollapse()
        fleet = game.fleet.ships.map { ShipView(it) }.toTypedArray()
        totalPopulation = game.fleet.ships.sumBy { it.crew }
        fleetSpeed = game.fleet.speed
    }

    @JsName("refreshStar")
    fun refreshStar() {
        val star = game.galaxy.getStarAt(game.fleet.location)
        currentSystem = star?.let { StarView(it) }

        // draw current system
        if (star != null) {
            systemRenderer.resize(star.planets.size*90 + 250, 200)

            val stage = PIXI.Container()
            when (star.type) {
                BINARY -> {
                    stage.addChild(Shapes.circle(Point(60, 100), 30.0, LineStyle.NONE, Color(255, 85, 0)))
                    stage.addChild(Shapes.circle(Point(140, 100), 40.0, LineStyle.NONE, Color(255, 255, 150)))
                }
                BLUE_GIANT -> stage.addChild(Shapes.circle(Point(100, 100), 50.0, LineStyle.NONE, Color(150, 255, 255)))
                BLUE_SUPERGIANT -> stage.addChild(Shapes.circle(Point(100, 100), 70.0, LineStyle.NONE, Color(150, 255, 255)))
                RED_DWARF -> stage.addChild(Shapes.circle(Point(100, 100), 30.0, LineStyle.NONE, Color(255, 85, 0)))
                RED_GIANT -> stage.addChild(Shapes.circle(Point(100, 100), 50.0, LineStyle.NONE, Color(255, 150, 100)))
                RED_SUPERGIANT -> stage.addChild(Shapes.circle(Point(100, 100), 70.0, LineStyle.NONE, Color(255, 150, 100)))
                TRINARY -> {
                    stage.addChild(Shapes.circle(Point(70, 75), 20.0, LineStyle.NONE, Color(255, 85, 0)))
                    stage.addChild(Shapes.circle(Point(130, 75), 25.0, LineStyle.NONE, Color(255, 255, 150)))
                    stage.addChild(Shapes.circle(Point(100, 125), 25.0, LineStyle.NONE, Color(150, 255, 255)))
                }
                WHITE_DWARF -> stage.addChild(Shapes.circle(Point(100, 100), 30.0, LineStyle.NONE, Color.WHITE))
                WHITE_STAR -> stage.addChild(Shapes.circle(Point(100, 100), 40.0, LineStyle.NONE, Color.WHITE))
                YELLOW_STAR -> stage.addChild(Shapes.circle(Point(100, 100), 40.0, LineStyle.NONE, Color(255, 255, 150)))
            }

            var x = 250
            for (planet in star.planets) {
                stage.addChild(Shapes.circle(Point(100, 100), x - 100.0, LineStyle(Color.WHITE)))

                val pos = Point(x, 100)
                when (planet.type) {
                    ARCTIC -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(200, 255, 255)))
                    }
                    ARID -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(240, 240, 125)))
                    }
                    CRYSTALLINE -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(255, 100, 175)))
                    }
                    DESERT -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(220, 175, 100)))
                    }
                    HELIUM_GIANT -> {
                        stage.addChild(Shapes.circle(pos, 35.0, LineStyle(Color(240, 240, 125))))
                        stage.addChild(Shapes.circle(pos, 25.0, LineStyle.NONE, Color(200, 175, 75)))
                    }
                    HYDROGEN_GIANT -> {
                        stage.addChild(Shapes.circle(pos, 25.0, LineStyle.NONE, Color(220, 150, 50)))
                    }
                    JUNGLE -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(50, 200, 50)))
                    }
                    LAVA -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(200, 75, 0)))
                    }
                    METHANE_GIANT -> {
                        stage.addChild(Shapes.circle(pos, 25.0, LineStyle.NONE, Color(50, 175, 225)))
                    }
                    OCEAN -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(0, 100, 255)))
                    }
                    ROCKY -> {
                        stage.addChild(Shapes.circle(pos, 10.0, LineStyle.NONE, Color(150, 150, 150)))
                    }
                    TERRAN -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(125, 200, 225)))
                    }
                    TUNDRA -> {
                        stage.addChild(Shapes.circle(pos, 15.0, LineStyle.NONE, Color(50, 200, 150)))
                    }
                }

                x += 90
            }

            systemRenderer.render(stage)
        }
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
        selectedShip = shipView
        jQuery("#shipDetails").collapse("show")
    }

    @JsName("closeShipCollapse")
    fun closeShipCollapse() {
        selectedShip = null
        jQuery("#shipDetails").collapse("hide")
    }

    @JsName("renameSelectedShip")
    fun renameSelectedShip(name: String){
        selectedShip?.apply { ship.rename(name) }
    }

    @JsName("activeClass")
    fun activeClass(shipView: ShipView) = if (shipView == selectedShip) "table-primary" else ""

    @JsName("setDestination")
    fun setDestination() {
        game.fleet.destination = selectedDestination.toIntVector()
    }

    @JsName("resetSelectedDestination")
    fun resetSelectedDestination() {
        selectedDestination = game.fleet.destination.toMutVector()
    }

    @JsName("nextDay")
    fun nextDay() {
        game.nextDay()
    }

    private fun util.IntVector2.toPoint(): Point =
            Point(x * galaxyRenderer.width.toInt() / game.galaxy.mapSize, y * galaxyRenderer.height.toInt() / game.galaxy.mapSize)
}
