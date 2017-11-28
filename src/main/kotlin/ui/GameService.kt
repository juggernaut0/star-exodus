package ui

import angular.HttpService
import game.ExodusGame
import serialization.JsonSerializer
import util.Event
import util.EventEmitter
import kotlin.browser.window

class GameService : EventEmitter<GameService>() {
    lateinit var game: ExodusGame
        private set
    val ready get() = ::game.isInitialized

    private var saveCleared = false

    private val onReady: Event<GameService, Unit> = Event(this)

    init {
        window.onbeforeunload = { if (!saveCleared) saveGame(); null }
    }

    // Solves problem if game is ready before controllers have a chance to initialize & register handlers
    fun initWhenReady(handler: (GameService, Unit) -> Unit) {
        if (ready) {
            handler(this, Unit)
        } else {
            onReady += handler
        }
    }

    fun loadOrCreate(http: HttpService): GameService {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                JsonSerializer.load(savedString)
            } else {
                ExodusGame(loader)
            }
            onReady(Unit)
        })
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
}
