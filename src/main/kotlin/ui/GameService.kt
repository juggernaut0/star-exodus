package ui

import game.ExodusGame
import kotlinx.serialization.SerializationException
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

    val eventLog = EventLog(this)

    lateinit var currentSystem: StarView
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
            ExodusGame.resources = loader
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                try {
                    JsonSerializer.load(savedString)
                } catch (e: SerializationException) {
                    console.error("Cannot load game, generating new game: ", e)
                    ExodusGame()
                }
            } else {
                ExodusGame()
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
        currentSystem = game.fleet.currentLocation.toView()
    }
}
