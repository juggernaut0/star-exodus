package ui

import angular.HttpService
import game.ResourceLoader
import game.ShipClass
import kotlin.js.Promise

class HttpResourceLoader(private val http: HttpService) : ResourceLoader {
    private lateinit var starNames: List<String>
    private lateinit var shipNames: List<String>
    private lateinit var shipClasses: List<ShipClass>

    fun fetchResources(): Promise<Unit> {
        val stars = http.get<String>("/star_names.txt").then({ starNames = it.data.split('\n').map { it.trim() }.filter { it.isNotEmpty() } })
        val ships = http.get<String>("/ship_names.txt").then({ shipNames = it.data.split('\n').map { it.trim() }.filter { it.isNotEmpty() } })
        return Promise.all(arrayOf(stars, ships)).then({})
    }

    override fun getStarNames(): List<String> = starNames

    override fun getShipNames(): List<String> = shipNames
}
