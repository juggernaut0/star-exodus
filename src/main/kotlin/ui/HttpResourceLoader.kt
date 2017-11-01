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
        val stars = http.get<String>("/star_names.txt").then({ starNames = it.data.split('\n') })
        val ships = http.get<String>("/ship_names.txt").then({ shipNames = it.data.split('\n') })
        val classes = http.get<String>("/ship_classes.txt").then({ shipClasses = parseShipClasses(it.data) })
        return Promise.all(arrayOf(stars, ships, classes)).then({})
    }

    private fun parseShipClasses(file: String): List<ShipClass> {
        return file.split('\n').filter { it.trim().isNotEmpty() }.map { line ->
            val fields = line.split(',').map { it.trim() }
            val intFields = fields.slice(1..10).map { it.toInt() }
            ShipClass(
                    name = fields[0],
                    maxCrew = intFields[0],
                    minCrew = intFields[1],
                    maxHull = intFields[2],
                    speed = intFields[3],
                    cargoCapacity = intFields[4],
                    upgradeSlots = intFields[5],
                    smSlots = intFields[6],
                    mdSlots = intFields[7],
                    lgSlots = intFields[8],
                    hanger = intFields[9]
            )
        }
    }

    override fun getStarNames(): List<String> = starNames

    override fun getShipNames(): List<String> = shipNames

    override fun getShipClasses(): List<ShipClass> = shipClasses
}