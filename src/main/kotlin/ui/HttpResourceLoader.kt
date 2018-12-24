package ui

import game.ResourceLoader
import util.HttpClient
import kotlin.js.Promise

class HttpResourceLoader(private val http: HttpClient) : ResourceLoader {
    private lateinit var starNames: List<String>
    private lateinit var shipNames: List<String>

    fun fetchResources(): Promise<Unit> {
        val stars = http.get("/star_names.txt").then { data -> starNames = data.split('\n').map { it.trim() }.filter { it.isNotEmpty() } }
        val ships = http.get("/ship_names.txt").then { data -> shipNames = data.split('\n').map { it.trim() }.filter { it.isNotEmpty() } }
        return Promise.all(arrayOf(stars, ships)).then {}
    }

    override fun getStarNames(): List<String> = starNames

    override fun getShipNames(): List<String> = shipNames
}
