package serialization

import game.InventoryItem
import game.PlanetFeature
import game.PlanetType
import game.StarType
import util.Location

// homebrew reflection to iterate properties and get name as string
abstract class SerializationModel(vararg val props: Pair<String, Any>) {
    protected val propMap = mutableMapOf(*props)
}

data class SGame(val galaxy: SGalaxy, val fleet: SFleet, val day: Int)
    : SerializationModel("galaxy" to galaxy, "fleet" to fleet, "day" to day)

data class SGalaxy(val stars: List<SStarSystem>, val mapSize: Int)
    : SerializationModel("stars" to stars, "mapSize" to mapSize)

data class SStarSystem(val name: String, val location: Location, val type: StarType, val planets: List<SPlanet>)
    : SerializationModel("name" to name, "location" to location, "type" to type, "planets" to planets)

data class SPlanet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, val exploration: Int)
    : SerializationModel("name" to name, "type" to type, "features" to features, "exploration" to exploration)

data class SFleet(val ships: List<SShip>, val location: Location)
    : SerializationModel("ships" to ships, "location" to location)

data class SShip(val name: String, val shipClass: String, val hullPoints: Int, val crew: Int, val inventory: SInventory)
    : SerializationModel("name" to name, "class" to shipClass, "hullPoints" to hullPoints, "crew" to crew, "inventory" to inventory)

data class SInventory(val capacity: Int, val contents: Map<InventoryItem, Int>)
    : SerializationModel("capacity" to capacity, "contents" to contents)
