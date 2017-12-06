package ui

import PIXI.SystemRenderer
import angular.Scope

@Suppress("MemberVisibilityCanPrivate", "unused")
class StarExodusController(val scope: Scope, val gameService: GameService) {
    private val galaxyRenderer: SystemRenderer

    var confirmMessage: String = ""
    var confirmAction: () -> Unit = {}

    var clickedStar: StarView? = null
    var currentSystem: StarView? = null

    init {
        gameService.onReady += { _, _ ->
            refreshMap()
            refreshCurrentSystem()
        }

        galaxyRenderer = initPixi("mapPanel", 800, 800)
    }

    @JsName("refreshMap")
    fun refreshMap() {
        val stage = PIXI.Container()

        gameService.game.fleet.discoveredStars
                .asSequence()
                .map {
                    Shapes.circle(it.location.toPoint(), 2.0, lineStyle = LineStyle(Color.TRANSPARENT), fillColor = Color.WHITE) { _ ->
                        clickedStar = StarView(it)
                        scope.apply()
                    }
                }
                .forEach { stage.addChild(it) }

        stage.addChild(Shapes.circle(gameService.game.fleet.location.toPoint(), 4.0, lineStyle = LineStyle(Color.RED, 2.0)))

        galaxyRenderer.render(stage)
    }

    private fun refreshCurrentSystem() {
        currentSystem = gameService.game.galaxy.getStarAt(gameService.game.fleet.location)?.let { StarView(it) }
    }

    @JsName("nextDay")
    fun nextDay() {
        gameService.game.nextDay()
        refreshCurrentSystem()
    }

    @JsName("saveGame")
    fun saveGame() {
        gameService.saveGame()
    }

    @JsName("clearSave")
    fun clearSave() {
        gameService.clearSavedGame()
    }

    private fun util.IntVector2.toPoint(): Point =
            Point(x * galaxyRenderer.width.toInt() / gameService.game.galaxy.mapSize, y * galaxyRenderer.height.toInt() / gameService.game.galaxy.mapSize)
}
