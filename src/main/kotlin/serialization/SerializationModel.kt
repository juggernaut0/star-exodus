package serialization

import game.InventoryItem
import game.PlanetFeature
import game.PlanetType
import game.StarType
import util.Location
import kotlin.reflect.KClass

interface SerializationModel

// simplified KType
class Type<out T : Any>(val classifier: KClass<out T>, val arguments: List<Type<*>> = emptyList())

class Property<out T : Any>(val name: String, val type: Type<T>) {
    fun getValue(instance: Any): T = instance.asDynamic()[name] as T
}

// homebrew reflection for properties & constructors
object SerializationModels {
    val props = mapOf(
            SGame::class to arrayOf(
                    Property("galaxy", Type(SGalaxy::class)),
                    Property("fleet", Type(SFleet::class)),
                    Property("day", Type(Int::class))
            ),
            SGalaxy::class to arrayOf(
                    Property("stars", Type(List::class, listOf(Type(SStarSystem::class)))),
                    Property("mapSize", Type(Int::class))
            ),
            SStarSystem::class to arrayOf(
                    Property("name", Type(String::class)),
                    Property("location", Type(Location::class)),
                    Property("type", Type(StarType::class)),
                    Property("planets", Type(List::class, listOf(Type(SPlanet::class))))
            ),
            SPlanet::class to arrayOf(
                    Property("name", Type(String::class)),
                    Property("type", Type(PlanetType::class)),
                    Property("features", Type(List::class, listOf(Type(PlanetFeature::class)))),
                    Property("exploration", Type(Int::class))
            ),
            SFleet::class to arrayOf(
                    Property("ships", Type(List::class, listOf(Type(SShip::class)))),
                    Property("location", Type(Location::class))
            ),
            SShip::class to arrayOf(
                    Property("name", Type(String::class)),
                    Property("shipClass", Type(String::class)),
                    Property("hullPoints", Type(Int::class)),
                    Property("crew", Type(Int::class)),
                    Property("inventory", Type(SInventory::class))
            ),
            SInventory::class to arrayOf(
                    Property("capacity", Type(Int::class)),
                    Property("contents", Type(Map::class, listOf(Type(InventoryItem::class), Type(Int::class))))
            )
    )
    @Suppress("UNCHECKED_CAST")
    val ctors = mapOf<KClass<out SerializationModel>, (Array<Any>) -> SerializationModel>(
            SGame::class to { arr -> SGame(arr[0] as SGalaxy, arr[1] as SFleet, arr[2] as Int) },
            SGalaxy::class to { arr -> SGalaxy(arr[0] as List<SStarSystem>, arr[1] as Int) },
            SStarSystem::class to { arr -> SStarSystem(arr[0] as String, arr[1] as Location, arr[2] as StarType, arr[3] as List<SPlanet>) },
            SPlanet::class to { arr -> SPlanet(arr[0] as String, arr[1] as PlanetType, arr[2] as List<PlanetFeature>, arr[3] as Int) },
            SFleet::class to { arr -> SFleet(arr[0] as List<SShip>, arr[1] as Location) },
            SShip::class to { arr -> SShip(arr[0] as String, arr[1] as String, arr[2] as Int, arr[3] as Int, arr[4] as SInventory) },
            SInventory::class to { arr -> SInventory(arr[0] as Int, arr[1] as Map<InventoryItem, Int>) }
    )

    val enums = mapOf(
            PlanetType::class to PlanetType::valueOf,
            PlanetFeature::class to PlanetFeature::valueOf,
            StarType::class to StarType::valueOf,
            InventoryItem::class to InventoryItem::valueOf
    )

    class SGame(val galaxy: SGalaxy, val fleet: SFleet, val day: Int) : SerializationModel
    class SGalaxy(val stars: List<SStarSystem>, val mapSize: Int) : SerializationModel
    class SStarSystem(val name: String, val location: Location, val type: StarType, val planets: List<SPlanet>) : SerializationModel
    class SPlanet(val name: String, val type: PlanetType, val features: List<PlanetFeature>, val exploration: Int) : SerializationModel
    class SFleet(val ships: List<SShip>, val location: Location) : SerializationModel
    class SShip(val name: String, val shipClass: String, val hullPoints: Int, val crew: Int, val inventory: SInventory) : SerializationModel
    class SInventory(val capacity: Int, val contents: Map<InventoryItem, Int>) : SerializationModel
}
