package serialization

import game.*
import util.IntVector2

interface SerializationModel

object SerializationModels {
    class SGame(val galaxy: SGalaxy, val fleet: SFleet, val day: Int) : SerializationModel
    class SGalaxy(val stars: List<SStarSystem>, val mapSize: Int) : SerializationModel
    class SStarSystem(val name: String, val location: IntVector2, val type: StarType, val planets: List<SPlanet>) : SerializationModel
    class SPlanet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, val exploration: Int) : SerializationModel
    class SFleet(val ships: List<SShip>, val location: IntVector2, val destination: IntVector2) : SerializationModel
    class SShip(val name: String, val shipClass: ShipClass, val hullPoints: Int, val crew: Int, val inventory: SInventory, val exploring: Int?) : SerializationModel
    class SInventory(val capacity: Int, val contents: Map<InventoryItem, Int>) : SerializationModel
}
