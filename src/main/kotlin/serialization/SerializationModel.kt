package serialization

import game.InventoryItem
import game.PlanetFeature
import game.PlanetType
import game.StarType
import util.Location

interface SerializationModel

object SerializationModels {
    class SGame(val galaxy: SGalaxy, val fleet: SFleet, val day: Int) : SerializationModel
    class SGalaxy(val stars: List<SStarSystem>, val mapSize: Int) : SerializationModel
    class SStarSystem(val name: String, val location: Location, val type: StarType, val planets: List<SPlanet>) : SerializationModel
    class SPlanet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, val exploration: Int) : SerializationModel
    class SFleet(val ships: List<SShip>, val location: Location) : SerializationModel
    class SShip(val name: String, val shipClass: String, val hullPoints: Int, val crew: Int, val inventory: SInventory) : SerializationModel
    class SInventory(val capacity: Int, val contents: Map<InventoryItem, Int>) : SerializationModel
}
