package game

import kotlinx.serialization.Serializable
import serialization.*
import util.Event
import util.EventEmitter
import util.Random
import kotlin.math.*

class Ship(
        name: String,
        val shipClass: ShipClass,
        hullPoints: Int,
        crew: Int,
        val inventory: Inventory,
        var exploring: Planet?,
        weapons: List<Weapon>
) : EventEmitter<Ship>() {
    var name: String = name
        private set

    var hullPoints: Int = hullPoints
        private set
    inline val maxHull get() = shipClass.maxHull

    var crew: Int = crew
        private set
    inline val minCrew get() = shipClass.minCrew
    inline val maxCrew get() = shipClass.maxCrew

    val mass get() = maxCrew/2 + inventory.freeSpace/2 + inventory.usedSpace + shipClass.hanger*2

    val speed get() = shipClass.speed

    // fuel per distance unit
    val fuelConsumption get() = sqrt(mass.toDouble()) * FUEL_COEFFICIENT
    // food per turn
    val foodConsumption get() = ceil(crew * FOOD_COEFFICIENT).toInt()

    val explorers get() = min(floor(0.1 * crew).toInt(), MAX_EXPLORERS)

    val destroyed get() = hullPoints == 0

    var mining: MiningTarget? = null
        private set

    internal val _weapons: MutableList<Weapon> = weapons.toMutableList()
    val weapons: List<Weapon> get() = _weapons

    val onMine = Event<Ship, MiningEventArgs>().bind(this)
    val onRepair = Event<Ship, Int>().bind(this)
    val onBirth = Event<Ship, Int>().bind(this)
    val onDeath = Event<Ship, Int>().bind(this)

    fun rename(newName: String) {
        if (newName.isNotBlank()){
            name = newName
        }
    }

    // returns amount actually changed
    private fun modHullPoints(amt: Int): Int {
        val oldHp = hullPoints
        hullPoints = minOf(maxOf(hullPoints + amt, 0), maxHull)
        return hullPoints - oldHp
    }

    internal fun modCrew(amt: Int): Int {
        val oldCrew = crew
        crew = minOf(maxOf(crew + amt, 0), maxCrew)
        return crew - oldCrew
    }

    fun transferCrew(dest: Ship, amt: Int): Int {
        val actual = arrayOf(crew, dest.maxCrew - dest.crew, max(amt, 0)).min() ?: 0
        modCrew(-actual)
        dest.modCrew(actual)
        return actual
    }

    internal fun eatFood() {
        val curFood = inventory[InventoryItem.FOOD]
        if (curFood >= foodConsumption) {
            inventory.removeItems(InventoryItem.FOOD, foodConsumption)
        } else {
            val toKill = crew - (curFood / FOOD_COEFFICIENT).toInt()
            modCrew(-toKill)
            inventory.removeItems(InventoryItem.FOOD, curFood)
        }
    }

    fun fuelConsumption(distance: Int): Int {
        return ceil(fuelConsumption * distance).toInt()
    }

    internal fun consumeFuel(distance: Int): Boolean {
        val amt = fuelConsumption(distance)
        if (amt > inventory[InventoryItem.FUEL]) return false
        inventory.removeItems(InventoryItem.FUEL, amt)
        return true
    }

    fun miningYield(target: MiningTarget) = when (target.resource) {
        InventoryItem.FUEL -> target.planet.type.fuelGatherAmount * shipClass.fuelGatherMultiplier
        InventoryItem.FUEL_ORE, InventoryItem.METAL_ORE -> target.planet.oreAmount * shipClass.oreMultiplier
        InventoryItem.FOOD -> target.planet.type.foodGatherAmount * shipClass.foodGatherMutliplier
        else -> 0.0
    }.toInt()

    internal fun mine(target: MiningTarget) {
        fun mineAndInvoke(item: InventoryItem, amount: Int) {
            val actual = inventory.addItems(item, amount)
            if (actual > 0) {
                onMine(MiningEventArgs(target.planet, item, actual))
            }
        }

        val amt = miningYield(target)

        if (target.resource == InventoryItem.FUEL_ORE || target.resource == InventoryItem.METAL_ORE) {
            val fuelAmt = Random.range(amt / 2)
            val rareAmt = if (target.planet.discoveredFeatures.contains(PlanetFeature.RARE_ELEMENTS)) Random.range((amt - fuelAmt) / 2) else 0
            val metalAmt = amt - fuelAmt - rareAmt
            mineAndInvoke(InventoryItem.FUEL_ORE, fuelAmt)
            mineAndInvoke(InventoryItem.METAL_ORE, metalAmt)
            mineAndInvoke(InventoryItem.RARE_METALS, rareAmt)
        } else {
            mineAndInvoke(target.resource, amt)
        }

        mining = target
    }

    internal fun repair(passiveRepair: Int = 0) {
        val amt = intArrayOf(MAX_REPAIR_RATE, maxHull - hullPoints, floor(inventory[InventoryItem.METAL] / REPAIR_COST).toInt()).min() ?: 0
        val cost = ceil(amt * REPAIR_COST).toInt()
        val amtWPassive = amt + passiveRepair
        if (amtWPassive > 0 && inventory[InventoryItem.METAL] >= cost) {
            modHullPoints(amtWPassive)
            inventory.removeItems(InventoryItem.METAL, cost)
            onRepair(amtWPassive)
        }
    }

    internal fun damage(amt: Int) {
        modHullPoints(-amt)
    }

    private fun changePop(rate: Double, increase: Boolean): Int {
        val chance = (crew * (rate/365000.0) * Random.normal(1.0, 0.5)).coerceAtLeast(0.0)
        val whole = floor(chance).toInt()
        val frac = chance - whole
        val amt = whole + if (Random.chance(frac)) 1 else 0
        modCrew((if (increase) 1 else -1) * amt)
        return amt
    }

    internal fun births() {
        if (shipClass.military) return
        if (crew < 5) return

        val amt = changePop(BIRTH_RATE, true)
        if (amt > 0) {
            onBirth(amt)
        }
    }

    internal fun deaths() {
        if (shipClass.military) return

        val amt = changePop(DEATH_RATE, false)
        if (amt > 0) {
            onDeath(amt)
        }
    }

    companion object {
        const val MAX_EXPLORERS = 50 // per ship
        const val FUEL_COEFFICIENT = 0.01
        const val FOOD_COEFFICIENT = 0.008 // food per person per turn
        const val REPAIR_COST = 0.5 // per hull point
        const val BIRTH_RATE = 18.0 // per 1000 people per year
        const val DEATH_RATE = 11.0 // per 1000 people per year
        const val MAX_REPAIR_RATE = 20 // hp per day

        operator fun invoke(name: String, shipClass: ShipClass): Ship {
            val hull = (shipClass.maxHull * Random.range(0.7, 1.0)).toInt()
            val crew = (shipClass.maxCrew * Random.range(0.6, 0.9)).toInt()
            val inv = Inventory(shipClass.cargoCapacity)
            inv.addItems(InventoryItem.FUEL, (inv.capacity/4)+5)
            inv.addItems(InventoryItem.FOOD, inv.capacity/5)

            fun fillSlots(weaponType: WeaponType) = List(shipClass.weaponSlots(weaponType)) {
                Random.choice(Weapon.values().filter { it.type == weaponType })
            }
            val weapons = fillSlots(WeaponType.SMALL) + fillSlots(WeaponType.MEDIUM) + fillSlots(WeaponType.LARGE)

            return Ship(name, shipClass, hull, crew, inv, null, weapons)
        }
    }

    data class MiningTarget(val planet: Planet, val resource: InventoryItem)
    data class MiningEventArgs(val planet: Planet, val resource: InventoryItem, val amount: Int)

    object Serial : Serializer<Ship, Serial.Data> {
        @Serializable
        class Data(
                val name: String,
                val shipClass: ShipClass,
                val hullPoints: Int,
                val crew: Int,
                val inventory: Inventory.Serial.Data,
                val exploring: Int?,
                val weapons: List<Weapon>
        )

        override fun save(model: Ship, refs: RefSaver): Data {
            return Data(
                    model.name,
                    model.shipClass,
                    model.hullPoints,
                    model.crew,
                    Inventory.Serial.save(model.inventory, refs),
                    model.exploring?.let { refs.savePlanetRef(it) },
                    model.weapons
            )
        }

        override fun load(data: Data, refs: RefLoader): Ship {
            return Ship(data.name,
                    data.shipClass,
                    data.hullPoints,
                    data.crew,
                    Inventory.Serial.load(data.inventory, refs),
                    data.exploring?.let { refs.loadPlanetRef(it) },
                    data.weapons
            )
        }
    }
}
