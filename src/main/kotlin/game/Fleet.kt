package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

class Fleet(
        ships: Collection<Ship>,
        private val galaxy: Galaxy,
        private var ftlTargetIndex: Int? = null, // only set when warming up
        private var ftlWarmupProgress: Int = 0,
        private var ftlCooldownProgress: Int = 0,
        private val timers: MutableMap<SystemArrivalEvent, Int> = mutableMapOf()
) : EventEmitter<Fleet>() {
    private val _ships = ships.toMutableList()
    val ships: Collection<Ship> get() = _ships

    val currentLocation get() = galaxy.main.current
    val destinations get() = galaxy.main.next

    val speed: Int get() = ships.asSequence().map { it.shipClass.speed }.min() ?: 0
    val ftlWarmupTimeRemaining get() = ftlWarmupProgress
    val ftlCooldownTimeRemaining get() = ftlCooldownProgress
    val isFtlReady get() = ftlCooldownTimeRemaining == 0
    val ftlTargetDestination get() = ftlTargetIndex?.let { galaxy.main.next[it] }

    // TODO serialize combat state
    var blockedState: BlockedState? = null
        private set

    val onArrive = event<Fleet, ArriveEventArgs>()
    val onTimerFinished = event<Fleet, SystemArrivalEvent>()

    fun doTurn() {
        abandonUncrewed()
        growFood()
        eatFood()
        ships.forEach { it.births(); it.deaths() }

        ships.forEach { it.repair(galaxy.main.current.passiveRepair) }

        val ti = ftlTargetIndex
        if (ti != null) {
            if (ftlWarmupTimeRemaining <= 1) {
                val dist = galaxy.main.next[ti].distance
                galaxy.main.advance(ti)
                for (ship in ships) {
                    if (!ship.consumeFuel(dist)) {
                        abandonShip(ship)
                    }

                    ship.exploring = null
                }
                ftlTargetIndex = null
                ftlCooldownProgress = ftlCooldownTime(dist)
                ftlWarmupProgress = 0

                timers.clear()

                val newStar = galaxy.main.current
                val arrivalEvent = SystemArrivalEvent.generateAndExecute(this)

                onArrive(ArriveEventArgs(newStar, arrivalEvent))
            } else {
                ftlWarmupProgress--
            }
        } else if (ftlCooldownTimeRemaining > 0) {
            ftlCooldownProgress--
        }

        for (event in timers.keys.toSet()) {
            val days = timers[event]!!
            if (days == 0) {
                event.onTimerFinished(this)
                timers.remove(event)
                onTimerFinished(event)
            } else {
                timers[event] = days - 1
            }
        }

        exploreSystem()
        gatherResources()
    }

    fun abandonShip(ship: Ship) {
        _ships.remove(ship)
    }

    private fun abandonUncrewed() {
        val uncrewed = _ships.filter { it.crew < it.shipClass.minCrew }
        var remaining = uncrewed.sumBy { it.crew }
        _ships.removeAll(uncrewed)

        val notFull = _ships.filter { it.crew < it.shipClass.maxCrew }.toMutableList()
        while (remaining > 0) {
            if (notFull.size == 0) break

            val ship = Random.choice(notFull)
            ship.modCrew(1)
            if (ship.crew == ship.shipClass.maxCrew) {
                notFull.remove(ship)
            }
            remaining--
        }
    }

    private fun growFood() {
        ships.forEach { it.inventory.addItems(InventoryItem.FOOD, it.shipClass.foodProduction) }
    }

    private fun eatFood() {
        ships.forEach { it.eatFood() }
    }

    fun startFtl(destIndex: Int) {
        if (!isFtlReady) throw IllegalStateException("Cannot start FTL if not ready")
        if (destIndex !in galaxy.main.next.indices) throw IndexOutOfBoundsException()
        ftlTargetIndex = destIndex
        ftlWarmupProgress = FTL_WARMUP
        ftlCooldownProgress = 0
    }

    fun cancelFtl() {
        ftlTargetIndex = null
        ftlWarmupProgress = 0
    }

    private fun ftlCooldownTime(distance: Int) = ceil(FTL_COOLDOWN_FACTOR * distance / speed).toInt()

    private fun exploreSystem() {
        val explorers = ships.groupBy { it.exploring }
        currentLocation.planets.forEach { planet -> planet.explore(explorers[planet] ?: emptyList()) }
    }

    private fun gatherResources() {
        // match ships to best planet
        for (ship in ships) {
            for (planet in currentLocation.planets) {
                val food = Ship.MiningTarget(planet, InventoryItem.FOOD)
                val ore = Ship.MiningTarget(planet, InventoryItem.METAL_ORE)
                val fuel = Ship.MiningTarget(planet, InventoryItem.FUEL)

                val foodYield = ship.miningYield(food)
                val oreYield = ship.miningYield(ore)
                val fuelYield = ship.miningYield(fuel)

                sequenceOf(food to foodYield, ore to oreYield, fuel to fuelYield)
                        .filter { (_, y) -> y > 0 }
                        .maxBy { (_, y) -> y }
                        ?.let { (t, _) -> ship.mine(t) }
            }
        }
    }

    fun autoSupply() {
        autoSupply(InventoryItem.FUEL) { it.fuelConsumption(400) }
        autoSupply(InventoryItem.FOOD) { it.foodConsumption }
    }

    private fun autoSupply(item: InventoryItem, consFn: (Ship) -> Int) {
        val total = ships.sumBy { it.inventory[item] }
        val totalCons = ships.sumBy(consFn)

        val days = ceil(total.toDouble() / totalCons).toInt()

        fun shipsToSurplus() = ships.asSequence().map{ it to it.inventory[item] - consFn(it) * days }
        fun has() = shipsToSurplus().find { it.second > 0 }
        fun needs() = shipsToSurplus().find { it.second < 0 && it.first.inventory.freeSpace > 0 }

        var surplus = has()
        var deficit = needs()
        while (surplus != null && deficit != null) {
            val amt = min(surplus.second, -deficit.second)
            surplus.first.inventory.transferItemsTo(deficit.first.inventory, item, amt)
            surplus = has()
            deficit = needs()
        }
    }

    internal fun startCombat() {
        if (blockedState != null) throw IllegalStateException("Cannot start combat when blocked")
        blockedState = BlockedState.Combat() // TODO combat strength
    }

    internal fun setHailed(hailed: BlockedState.Hailed) {
        if (blockedState != null) throw IllegalStateException("Cannot be hailed when blocked")
        blockedState = hailed
    }

    // TODO remove when combat is implemented
    fun endBlocker() {
        blockedState = null
    }

    internal fun startTimer(event: SystemArrivalEvent, days: Int) {
        timers[event] = days
    }

    companion object {
        // Days to warmup FTL
        private const val FTL_WARMUP = 4
        private const val FTL_COOLDOWN_FACTOR = 1.5

        operator fun invoke(numShips: Int): Fleet {
            val home = WeightedList(
                    ShipClass.SMALL_COLONY_SHIP to 6,
                    ShipClass.LARGE_COLONY_SHIP to 9,
                    ShipClass.LIVESHIP to 4,
                    ShipClass.CITYSHIP to 1
            )

            val capital = WeightedList(
                    ShipClass.BATTLESHIP to 7,
                    ShipClass.DREADNOUGHT to 6,
                    ShipClass.FLEET_CARRIER to 3,
                    ShipClass.TITAN to 3,
                    ShipClass.BATTLECARRIER to 1
            )

            val escort = WeightedList(
                    ShipClass.CORVETTE to 20,
                    ShipClass.SCOUT to 14,
                    ShipClass.DESTROYER to 16,
                    ShipClass.TROOP_CARRIER to 4,
                    ShipClass.FRIGATE to 8,
                    ShipClass.CRUISER to 6,
                    ShipClass.HEAVY_CRUISER to 6,
                    ShipClass.CARRIER to 4
            )

            val civilian = WeightedList(
                    ShipClass.SMALL_PASSENGER_CARRIER to 12,
                    ShipClass.MEDIUM_PASSENGER_CARRIER to 10,
                    ShipClass.LARGE_PASSENGER_CARRIER to 8,
                    ShipClass.HUGE_PASSENGER_CARRIER to 6,
                    ShipClass.CRUISE_LINER to 4,
                    ShipClass.DREAM_LINER to 2,
                    ShipClass.SMALL_FREIGHT_CARRIER to 8,
                    ShipClass.MEDIUM_FREIGHT_CARRIER to 6,
                    ShipClass.LARGE_FREIGHT_CARRIER to 4,
                    ShipClass.HUGE_FREIGHT_CARRIER to 2,
                    ShipClass.SUPER_FREIGHT_CARRIER to 1,
                    ShipClass.REFINERY_SHIP to 3,
                    ShipClass.FUEL_TANKER to 4,
                    ShipClass.MOBILE_DRY_DOCK to 1,
                    ShipClass.MINING_SHIP to 3
            )

            val numEscort = ((numShips - 2) * Random.range(0.2, 0.5)).roundToInt()
            val numCivilian = numShips - 2 - numEscort

            val classes = listOf(
                    Random.choice(home),
                    Random.choice(capital),
                    *Random.sample(escort, numEscort).toTypedArray(),
                    *Random.sample(civilian, numCivilian).toTypedArray()
            )

            val shipNames = ExodusGame.resources.getShipNames()
            val ships = Random.sample(shipNames, numShips)
                    .zip(classes)
                    .mapTo(mutableListOf()) { (name, cls) -> Ship(name, cls) }

            return Fleet(ships, Galaxy())
        }
    }

    object Serial : Serializer<Fleet, Serial.Data> {
        @Serializable
        class Data(
                val ships: List<Int>,
                val galaxy: Galaxy.Serial.Data,
                val ftlTargetIndex: Int?,
                val ftlWarmupProgress: Int,
                val ftlCooldownProgress: Int
        )

        override fun save(model: Fleet, refs: RefSaver): Data {
            return Data(
                    model.ships.map { refs.saveShipRef(it) },
                    Galaxy.Serial.save(model.galaxy, refs),
                    model.ftlTargetIndex,
                    model.ftlWarmupProgress,
                    model.ftlCooldownProgress
            )
        }

        override fun load(data: Data, refs: RefLoader): Fleet {
            return Fleet(
                    data.ships.map { refs.loadShipRef(it) },
                    Galaxy.Serial.load(data.galaxy, refs),
                    data.ftlTargetIndex,
                    data.ftlWarmupProgress,
                    data.ftlCooldownProgress
            )
        }
    }

    class ArriveEventArgs(val star: StarSystem, val arrivalEvent: SystemArrivalEvent)
}
