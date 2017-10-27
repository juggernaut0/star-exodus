package ui

import angular.HttpService
import game.ResourceLoader
import kotlin.js.Promise

class HttpResourceLoader(private val http: HttpService) : ResourceLoader {
    private lateinit var starNames: List<String>

    fun fetchResources(): Promise<Unit> {
        // NOTE: use Promise.all for multiple
        return http.get<String>("/star_names.txt").then({ starNames = it.data.split('\n') })
    }

    override fun getStarNames(): List<String> {
        return starNames
    }

    override fun getShipNames(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}