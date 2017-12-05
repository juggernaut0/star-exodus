package ui

import PIXI.SystemRenderer
import angular.Scope
import game.ExodusGame
import util.toTitleCase

@Suppress("MemberVisibilityCanPrivate", "unused")
class StarExodusController(val scope: Scope, val gameService: GameService) {
    private val galaxyRenderer: SystemRenderer

    private val _log: MutableList<String> = mutableListOf("Welcome to Star Exodus!")

    var confirmMessage: String = ""
    var confirmAction: () -> Unit = {}

    val log: Array<String> get() = _log.asReversed().toTypedArray()
    var clickedStar: StarView? = null
    var currentSystem: StarView? = null

    init {
        gameService.onReady += { sender, _ ->
            registerGameListeners(sender.game)
            refreshMap()
            refreshCurrentSystem()
        }

        galaxyRenderer = initPixi("mapPanel", 800, 800)
    }

    private fun registerGameListeners(game: ExodusGame) {
        game.onTurn += { sender, _ -> _log.add("Day ${sender.day}") }
        game.fleet.onArrive += { _, star -> _log.add("The fleet has arrived in the ${star.name} system.") }
        game.galaxy.stars
                .flatMap { it.planets }
                .forEach { it.onDiscoverFeature += { sender, feature -> _log.add("${feature.name.toTitleCase()} has been discovered on ${sender.name}.") } }
        for (ship in game.fleet.ships) {
            ship.onMine += { sender, args -> _log.add("${sender.name} has gathered ${args.amount} ${args.resource.name.toTitleCase()} from ${args.planet.name}.") }
            ship.onRepair += { sender, amt -> _log.add("${sender.name} has repaired for $amt hull points.") }
        }
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
