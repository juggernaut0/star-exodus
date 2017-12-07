package ui

import PIXI.SystemRenderer
import game.PlanetType
import game.StarType
import jQuery
import util.IntVector2
import util.toTypedArray
import kotlin.math.ceil

@Suppress("MemberVisibilityCanPrivate", "unused")
class SystemController(val gameService: GameService) {
    private val systemRenderer: SystemRenderer = initPixi("systemMap", 400, 200)

    var selectedPlanet: PlanetView? = null
    val destinationDisplay: String
        get() {
            if (!gameService.ready) return ""
            val dest = gameService.game.fleet.destination
            return gameService.game.galaxy.getStarAt(dest)?.name ?: dest.toDisplayString()
        }
    val destinationEta: Int
        get() {
            if (!gameService.ready) return 0
            return gameService.game.fleet.run { ceil(IntVector2.distance(location, destination) / speed).toInt() }
        }

    var ships: Array<ShipView> = emptyArray()
    var tradeShip: ShipView? = null

    init {
        gameService.onReady += { _, _ ->
            refreshStar()
            refreshFleet()

            gameService.game.onTurn += { _, _ -> refreshStar() }
        }
    }

    @JsName("refreshStar")
    fun refreshStar() {
        closePlanetCollapse()
        val star = gameService.game.galaxy.getStarAt(gameService.game.fleet.location)

        // draw current system
        if (star != null) {
            systemRenderer.resize(star.planets.size*90 + 250, 200)

            val stage = PIXI.Container()
            when (star.type) {
                StarType.BINARY -> {
                    stage.addChild(Shapes.circle(Point(60, 100), 30.0, fillColor = Color(255, 85, 0)))
                    stage.addChild(Shapes.circle(Point(140, 100), 40.0, fillColor = Color(255, 255, 150)))
                }
                StarType.BLUE_GIANT -> stage.addChild(Shapes.circle(Point(100, 100), 50.0, fillColor = Color(150, 255, 255)))
                StarType.BLUE_SUPERGIANT -> stage.addChild(Shapes.circle(Point(100, 100), 70.0, fillColor = Color(150, 255, 255)))
                StarType.RED_DWARF -> stage.addChild(Shapes.circle(Point(100, 100), 30.0, fillColor = Color(255, 85, 0)))
                StarType.RED_GIANT -> stage.addChild(Shapes.circle(Point(100, 100), 50.0, fillColor = Color(255, 150, 100)))
                StarType.RED_SUPERGIANT -> stage.addChild(Shapes.circle(Point(100, 100), 70.0, fillColor = Color(255, 150, 100)))
                StarType.TRINARY -> {
                    stage.addChild(Shapes.circle(Point(70, 75), 20.0, fillColor = Color(255, 85, 0)))
                    stage.addChild(Shapes.circle(Point(130, 75), 25.0, fillColor = Color(255, 255, 150)))
                    stage.addChild(Shapes.circle(Point(100, 125), 25.0, fillColor = Color(150, 255, 255)))
                }
                StarType.WHITE_DWARF -> stage.addChild(Shapes.circle(Point(100, 100), 30.0, fillColor = Color.WHITE))
                StarType.WHITE_STAR -> stage.addChild(Shapes.circle(Point(100, 100), 40.0, fillColor = Color.WHITE))
                StarType.YELLOW_STAR -> stage.addChild(Shapes.circle(Point(100, 100), 40.0, fillColor = Color(255, 255, 150)))
            }

            var x = 250
            for (planet in star.planets) {
                stage.addChild(Shapes.circle(Point(100, 100), x - 100.0, LineStyle(Color.WHITE)))

                val pos = Point(x, 100)
                when (planet.type) {
                    PlanetType.ARCTIC -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(200, 255, 255)))
                    PlanetType.ARID -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(240, 240, 125)))
                    PlanetType.CRYSTALLINE -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(255, 100, 175)))
                    PlanetType.DESERT -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(220, 175, 100)))
                    PlanetType.HELIUM_GIANT -> {
                        stage.addChild(Shapes.circle(pos, 35.0, lineStyle = LineStyle(Color(240, 240, 125))))
                        stage.addChild(Shapes.circle(pos, 25.0, fillColor = Color(200, 175, 75)))
                    }
                    PlanetType.HYDROGEN_GIANT -> stage.addChild(Shapes.circle(pos, 25.0, fillColor = Color(220, 150, 50)))
                    PlanetType.JUNGLE -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(50, 200, 50)))
                    PlanetType.LAVA -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(200, 75, 0)))
                    PlanetType.METHANE_GIANT -> stage.addChild(Shapes.circle(pos, 25.0, fillColor = Color(50, 175, 225)))
                    PlanetType.OCEAN -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(0, 100, 255)))
                    PlanetType.ROCKY -> stage.addChild(Shapes.circle(pos, 10.0, fillColor = Color(150, 150, 150)))
                    PlanetType.TERRAN -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(125, 200, 225)))
                    PlanetType.TUNDRA -> stage.addChild(Shapes.circle(pos, 15.0, fillColor = Color(50, 200, 150)))
                }

                x += 90
            }

            systemRenderer.render(stage)
        }
    }

    @JsName("refreshFleet")
    fun refreshFleet() {
        ships = gameService.game.fleet.ships.asSequence().map { ShipView(it) }.toTypedArray()
        tradeShip = ships.firstOrNull()
    }

    @JsName("planetRowClass")
    fun planetRowClass(planetView: PlanetView) = if (planetView == selectedPlanet) "table-primary" else ""

    @JsName("openPlanetCollapse")
    fun openPlanetCollapse(planetView: PlanetView) {
        selectedPlanet = planetView
        jQuery("#planetDetails").collapse("show")
    }

    @JsName("closePlanetCollapse")
    fun closePlanetCollapse() {
        selectedPlanet = null
        jQuery("#planetDetails").collapse("hide")
    }
}