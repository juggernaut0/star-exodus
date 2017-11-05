package serialization

import game.InventoryItem
import game.PlanetFeature
import game.PlanetType
import game.StarType
import util.Location

data class SGame(val galaxy: SGalaxy, val fleet: SFleet, val day: Int)
data class SGalaxy(val stars: List<SStarSystem>, val mapSize: Int)
data class SStarSystem(val name: String, val location: Location, val type: StarType, val planets: List<SPlanet>)
data class SPlanet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, val exploration: Int)
data class SFleet(val ships: List<SShip>, val location: Location)
data class SShip(val name: String, val shipClass: String, val hullPoints: Int, val crew: Int, val inventory: SInventory)
data class SInventory(val capacity: Int, val contents: Map<InventoryItem, Int>)
