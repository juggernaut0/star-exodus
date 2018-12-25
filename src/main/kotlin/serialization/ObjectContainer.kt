package serialization

import game.*
import kotlinx.serialization.Serializable

@Serializable
class ObjectContainer {
    var game: ExodusGame.Serial.Data? = null
    val starSystems: MutableList<StarSystem.Serial.Data> = mutableListOf()
    val planets: MutableList<Planet.Serial.Data> = mutableListOf()
    val ships: MutableList<Ship.Serial.Data> = mutableListOf()
}

class RefSaver {
    private val refs: MutableMap<Any, Int> = mutableMapOf()
    val objects = ObjectContainer()

    fun saveStarRef(obj: StarSystem): Int = saveRef(obj, StarSystem.Serial, objects.starSystems)
    fun savePlanetRef(obj: Planet): Int = saveRef(obj, Planet.Serial, objects.planets)
    fun saveShipRef(obj: Ship): Int = saveRef(obj, Ship.Serial, objects.ships)

    private fun <TM: Any, TD: Any> saveRef(obj: TM, ser: Serializer<TM, TD>, list: MutableList<TD>): Int {
        if (obj in refs) {
            return refs[obj]!!
        }

        val i = list.size
        refs[obj] = i
        list.add(ser.save(obj, this))
        return i
    }
}

class RefLoader(private val objects: ObjectContainer) {
    private val cache: MutableMap<Pair<Serializer<*, *>, Int>, Any> = mutableMapOf()

    fun loadStarRef(ref: Int) = loadRef(ref, StarSystem.Serial, objects.starSystems)
    fun loadPlanetRef(ref: Int) = loadRef(ref, Planet.Serial, objects.planets)
    fun loadShipRef(ref: Int) = loadRef(ref, Ship.Serial, objects.ships)

    private fun <TM: Any, TD: Any> loadRef(ref: Int, ser: Serializer<TM, TD>, list: MutableList<TD>): TM {
        return cache.getOrPut(ser to ref) { ser.load(list[ref], this) }.unsafeCast<TM>()
    }
}
