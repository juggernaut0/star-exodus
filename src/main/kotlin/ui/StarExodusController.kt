package ui

import PIXI.SystemRenderer
import angular.Scope
import util.MutVector2
import util.toTypedArray

@Suppress("MemberVisibilityCanPrivate", "unused")
class StarExodusController(val scope: Scope, val gameService: GameService) {
    private val galaxyRenderer: SystemRenderer

    private val _log: MutableList<String> = mutableListOf("Welcome to Star Exodus!")

    var confirmMessage: String = ""
    var confirmAction: () -> Unit = {}

    val log: Array<String> get() = _log.toTypedArray()
    var clickedStar: StarView? = null
    var currentSystem: StarView? = null

    init {
        gameService.onReady += { _, _ ->
            registerGameListeners()
            refreshMap()
            refreshCurrentSystem()
        }

        galaxyRenderer = initPixi("mapPanel", 800, 800)
    }

    private fun registerGameListeners() {
        // TODO
        gameService.game.onTurn += { game, _ -> _log.add("Day ${game.day}") }
    }

    @JsName("refreshMap")
    fun refreshMap() {
        val stage = PIXI.Container()

        gameService.game.galaxy.stars
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
