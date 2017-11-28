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
    var ready: Boolean = false
        private set

    val onReady: Event<GameService, Unit> = Event(this)

    fun loadOrCreate(http: HttpService): GameService {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({
            val savedString = window.localStorage.getItem("savedgame")
            game = if (savedString != null) {
                JsonSerializer.load(savedString)
            } else {
                ExodusGame(loader)
            }
            ready = true
            onReady(Unit)
        })
        return this
    }
}
