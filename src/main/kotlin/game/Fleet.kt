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
        groups: List<BattleGroup>,
        private val galaxy: Galaxy,
        private var ftlTargetIndex: Int? = null, // only set when warming up
        private var ftlWarmupProgress: Int = 0,
        private var ftlCooldownProgress: Int = 0,
        var gatherFocus: GatherFocus = GatherFocus.NONE,
        private val timers: MutableMap<SystemArrivalEvent, Int> = mutableMapOf(),
        private val spareWeapons: Counter<Weapon> = Counter()
) : EventEmitter<Fleet>() {
    private val _groups = groups.toMutableList()
    val groups: List<BattleGroup> get() = _groups
    val ships: Sequence<Ship> get() = groups.asSequence().flatMap { it.ships.asSequence() }

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

    private fun Ship.findGroup(): BattleGroup = groups.find { this in it.ships }!!

    fun abandonShip(ship: Ship) {
        ship.findGroup().abandonShip(ship)
    }

    private fun abandonUncrewed() {
        val allShips = ships.toMutableList()
        val uncrewed = allShips.filter { it.crew < it.shipClass.minCrew }
        if (uncrewed.isEmpty()) return

        for (ship in uncrewed) {
            abandonShip(ship)
        }
        allShips.removeAll(uncrewed)

        val notFull = allShips.filterTo(mutableListOf()) { it.crew < it.shipClass.maxCrew }
        var remaining = uncrewed.sumBy { it.crew }
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
        check(isFtlReady) { "Cannot start FTL if not ready" }
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
        val foodMult = if (gatherFocus == GatherFocus.FOOD) FOCUS_MUTLIPLIER else 1
        val oreMult = if (gatherFocus == GatherFocus.ORE) FOCUS_MUTLIPLIER else 1
        val fuelMult = if (gatherFocus == GatherFocus.FUEL) FOCUS_MUTLIPLIER else 1

        // match ships to best planet
        for (ship in ships) {
            currentLocation.planets
                    .asSequence()
                    .mapNotNull {
                        val food = Ship.MiningTarget(it, InventoryItem.FOOD)
                        val ore = Ship.MiningTarget(it, InventoryItem.METAL_ORE)
                        val fuel = Ship.MiningTarget(it, InventoryItem.FUEL)

                        val foodYield = ship.miningYield(food) * foodMult
                        val oreYield = ship.miningYield(ore) * oreMult
                        val fuelYield = ship.miningYield(fuel) * fuelMult

                        sequenceOf(food to foodYield, ore to oreYield, fuel to fuelYield)
                                .filter { (_, y) -> y > 0 }
                                .maxBy { (_, y) -> y }
                    }
                    .maxBy { (_, y) -> y }
                    ?.let { (t, _) -> ship.mine(t) }
        }
    }

    fun autoSupply() {
        autoSupply(InventoryItem.FUEL) { it.fuelConsumption(400) }
        autoSupply(InventoryItem.FOOD, capFn = { it.inventory.capacity / 2 }) { it.foodConsumption }
    }

    private fun autoSupply(item: InventoryItem, capFn: (Ship) -> Int = { it.inventory.capacity }, consFn: (Ship) -> Int) {
        val total = ships.sumBy { it.inventory[item] }
        val totalCons = ships.sumBy(consFn)

        val days = ceil(total.toDouble() / totalCons).toInt()

        fun shipsToSurplus() = ships.asSequence().map{ it to it.inventory[item] - (consFn(it) * days).coerceAtMost(capFn(it)) }
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

    internal fun startCombat(enemy: List<BattleGroup>) {
        check(blockedState == null) { "Cannot start combat when blocked" }
        blockedState = BlockedState.Combat(Battle(this, enemy))
    }

    internal fun setHailed(hailed: BlockedState.Hailed) {
        check(blockedState == null) { "Cannot be hailed when blocked" }
        blockedState = hailed
    }

    // TODO remove when combat is implemented
    fun endBlocker() {
        blockedState = null
    }

    internal fun startTimer(event: SystemArrivalEvent, days: Int) {
        timers[event] = days
    }

    fun spareWeapons(type: WeaponType): Set<Weapon> {
        return spareWeapons.asMap().keys.filterTo(mutableSetOf()) { it.type == type }
    }

    fun equipWeapon(ship: Ship, weapon: Weapon) {
        check(spareWeapons[weapon] > 0) { "Cannot equip a weapon that you do not have" }
        check(ship.weapons.count { it.type == weapon.type } < ship.shipClass.weaponSlots(weapon.type)) {
            "Ship has no open weapon slots of that type"
        }

        ship._weapons.add(weapon)
        spareWeapons[weapon] -= 1
    }

    fun unequipWeapon(ship: Ship, weapon: Weapon) {
        check(weapon in ship.weapons) { "Ship does not currently have that weapon equipped" }

        ship._weapons.remove(weapon)
        spareWeapons[weapon] += 1
    }

    companion object {
        // Days to warmup FTL
        private const val FTL_WARMUP = 4
        private const val FTL_COOLDOWN_FACTOR = 1.5

        private const val FOCUS_MUTLIPLIER = 3

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

            val shipNames = Deque(Random.sample(ExodusGame.resources.getShipNames(), numShips).shuffled())

            val capitalShip = Ship(shipNames.popFront(), Random.choice(capital))
            val escortShips = Random.sample(escort, numEscort).map { Ship(shipNames.popFront(), it) }
            val homeShip = Ship(shipNames.popFront(), Random.choice(home))
            val civilianShips = Random.sample(civilian, numCivilian).mapTo(mutableListOf()) { Ship(shipNames.popFront(), it) }
            civilianShips.add(homeShip)

            val groups = listOf(
                    BattleGroup("Capital Ships", listOf(capitalShip)),
                    BattleGroup("Escorts", escortShips),
                    BattleGroup("Civilians", civilianShips)
            )

            return Fleet(groups, Galaxy())
        }
    }

    object Serial : Serializer<Fleet, Serial.Data> {
        @Serializable
        class Data(
                val groups: List<BattleGroup.Serial.Data>,
                val galaxy: Galaxy.Serial.Data,
                val ftlTargetIndex: Int?,
                val ftlWarmupProgress: Int,
                val ftlCooldownProgress: Int,
                val gatherFocus: GatherFocus,
                val spareWeapons: Map<Weapon, Int>
        )

        override fun save(model: Fleet, refs: RefSaver): Data {
            return Data(
                    model.groups.map { BattleGroup.Serial.save(it, refs) },
                    Galaxy.Serial.save(model.galaxy, refs),
                    model.ftlTargetIndex,
                    model.ftlWarmupProgress,
                    model.ftlCooldownProgress,
                    model.gatherFocus,
                    model.spareWeapons.asMap()
            )
        }

        override fun load(data: Data, refs: RefLoader): Fleet {
            return Fleet(
                    data.groups.map { BattleGroup.Serial.load(it, refs) },
                    Galaxy.Serial.load(data.galaxy, refs),
                    data.ftlTargetIndex,
                    data.ftlWarmupProgress,
                    data.ftlCooldownProgress,
                    data.gatherFocus,
                    spareWeapons = Counter(data.spareWeapons)
            )
        }
    }

    class ArriveEventArgs(val star: StarSystem, val arrivalEvent: SystemArrivalEvent)

    enum class GatherFocus {
        NONE,
        FUEL,
        FOOD,
        ORE
    }
}
