package game
import util.Location

class Game(val galaxy: Galaxy, val fleet: Fleet, var day: Int)
class Galaxy(val mapSize: Int, val stars: MutableSet<StarSystem>)
class StarSystem(var name: String, val location: Location, val type: StarType, val planets: MutableList<Planet>)
class Planet(var name: String, val type: PlanetType, val features: MutableList<PlanetFeature>, var exploration: Int)
class Fleet(val ships: MutableSet<Ship>)
class Ship(var name: String, val shipClass: ShipClass, var hullPoints: Int, var crew: Int, val inventory: MutableMap<InventoryItem, Int>)
