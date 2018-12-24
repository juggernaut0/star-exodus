package ui

import game.ExodusGame
import serialization.JsonSerializer
import util.*
import kotlin.browser.window

class GameService : EventEmitter<GameService>() {
    lateinit var game: ExodusGame
        private set
    val ready get() = ::game.isInitialized

    private var saveCleared = false

    val onReady = oneTimeEvent<GameService, Unit>()
    val onFleetUpdate = event<GameService, Unit>()

    // TODO don't pass 'this' as param
    val eventLog = EventLog(this)

    var currentSystem: StarView? = null
        private set

    init {
        window.onbeforeunload = { if (!saveCleared) saveGame(); null }
    }

    private fun registerListeners() {
        game.onTurn += { _, _ -> setCurrentSystem() }
    }

    fun invokeFleetUpdate() {
        onFleetUpdate(Unit)
    }

    fun loadOrCreate(http: HttpClient): GameService {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then {
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                JsonSerializer.load(savedString)
            } else {
                ExodusGame(loader)
            }
            setCurrentSystem()
            registerListeners()
            onReady(Unit)
        }
        return this
    }

    fun saveGame() {
        window.localStorage.setItem("savedgame", JsonSerializer.save(game))
    }

    fun clearSavedGame() {
        window.localStorage.removeItem("savedgame")
        saveCleared = true
        window.location.reload()
    }

    private fun setCurrentSystem() {
        currentSystem = game.galaxy.getStarAt(game.fleet.location)?.let { StarView(it) }
    }

    fun findNearbyStars(): List<StarView> {
        if (!::game.isInitialized) return emptyList()

        return game.galaxy.getNearbyStars(game.fleet.location, game.fleet.speed * 10.0)
                .asSequence()
                .filter { it.location != game.fleet.location }
                .map { StarView(it) }
                .toList()
    }
}
