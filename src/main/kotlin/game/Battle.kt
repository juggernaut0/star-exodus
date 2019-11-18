package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.Deque
import util.Event
import util.EventEmitter
import util.Random
import kotlin.math.abs
import kotlin.math.roundToInt
class Battle private constructor(
        private val _zones: Array<MutableList<BattleGroup>>,
        private val initiativeOrder: Deque<BattleGroup>,
        private val initiativePoints: MutableMap<BattleGroup, Int>
) : EventEmitter<Battle>() {
    constructor(fleet: List<BattleGroup>, enemy: List<BattleGroup>) : this(
            _zones = Array(BATTLE_SIZE) {
                when(it) {
                    0 -> enemy.toMutableList()
                    BATTLE_SIZE - 1 -> fleet.toMutableList()
                    else -> mutableListOf()
                }
            },
            initiativeOrder = Deque((fleet + enemy).sortedBy { it.speed }),
            initiativePoints = (fleet + enemy).associateWithTo(mutableMapOf()) { 0 }
    ) {
        require(enemy.isNotEmpty()) { "Enemy cannot be empty" }
    }

    val zones: List<List<BattleGroup>> get() = _zones.asList()
    val turnOrder: List<BattleGroup> get() = initiativeOrder.toList() // TODO calculate actual order
    val currentGroup: BattleGroup get() = initiativeOrder.first()

    var finished = false
        private set

    val onCombatEvent = Event<Battle, String>().bind(this)

    init {
        advanceTurn()
    }

    fun executeTurn(tactic: Tactic, target: BattleGroup?) {
        // TODO AI
        require(!tactic.needsTarget || target != null) { "$tactic requires a target" }
        val group = currentGroup
        check(initiativePoints.getValue(group) >= POINTS_TO_EXECUTE) { "group must have enough points" }
        tactic.execute(this, group, target)
        if (checkVictory()) {
            finished = true
            return
        }
        modPoints(group, -POINTS_TO_EXECUTE)
        advanceTurn()
    }

    private fun advanceTurn() {
        while (true) {
            val group = initiativeOrder.peekFront()
            if (initiativePoints.getValue(group) >= POINTS_TO_EXECUTE) break
            modPoints(group, group.speed.coerceAtLeast(MIN_POINTS_PER_TURN))
            initiativeOrder.pushBack(initiativeOrder.popFront())
        }
    }

    private fun modPoints(group: BattleGroup, delta: Int) {
        initiativePoints[group] = initiativePoints.getValue(group) + delta
    }

    private fun checkVictory(): Boolean {
        return zones.flatten().all { !it.enemy }
    }

    // Positive amt = group moving "forward" = zone index decreasing
    private fun move(group: BattleGroup, amt: Int) {
        val src = zoneOf(group)
        val dest = (src - amt).coerceIn(zones.indices)
        _zones[src].remove(group)
        _zones[dest].add(group)
    }

    private fun attack(group: BattleGroup, target: BattleGroup) {
        val distance = distance(group, target)
        for (ship in group.ships) {
            for (weapon in ship.weapons) {
                val targetShip = Random.choice(target.ships)
                val chanceToHit = chanceToHit(weapon, targetShip, distance)
                val hits = Random.chances(chanceToHit, weapon.salvo)
                val totalDamage = weapon.damage * hits

                targetShip.damage(totalDamage)
                val destroyed = targetShip.destroyed
                if (destroyed) {
                    target.cleanupDestroyed()
                }

                // TODO move this message generation to UI, return a generic object for event
                val msg = "${ship.name}'s ${weapon.displayName} " + when {
                    hits == 0 -> "missed."
                    hits == 1 && weapon.salvo == 1 -> "hit the ${targetShip.name} for $totalDamage damage."
                    hits == 1 -> "hit the ${targetShip.name} once for $totalDamage damage."
                    else -> "hit the ${targetShip.name} $hits times for $totalDamage damage."
                } + if (destroyed) " The target was destroyed." else ""
                onCombatEvent(msg)

                if (target.ships.isEmpty()) {
                    if (!target.enemy) {
                        TODO("remove group from Fleet")
                    }
                    removeGroup(target)
                    return
                }
            }
        }
    }

    private fun removeGroup(group: BattleGroup) {
        val zone = zoneOf(group)
        _zones[zone].remove(group)
        initiativeOrder.remove(group)
        initiativePoints.remove(group)
    }

    fun estimateDamageAgainst(target: BattleGroup): IntRange {
        val group = currentGroup
        val distance = distance(group, target)
        return group.ships
                .flatMap { it.weapons }
                .map { weapon -> target.ships.map { ship -> approxDamage(weapon, ship, distance).roundToInt() }.minmax()!! }
                .reduce { a, b -> a rangePlus b }
    }

    private fun zoneOf(group: BattleGroup): Int = zones.indexOfFirst { group in it }
    private fun distance(a: BattleGroup, b: BattleGroup): Int = abs(zoneOf(a) - zoneOf(b))

    companion object {
        const val BATTLE_SIZE = 7
        private const val POINTS_TO_EXECUTE = 250
        private const val MIN_POINTS_PER_TURN = 20

        private fun chanceToHit(weapon: Weapon, target: Ship, distance: Int?): Double {
            val accuracy = if (distance != null) weapon.accuracy.atDistance(distance) else weapon.accuracy.atOptimalDistance()
            return (weapon.tracking.toDouble() / target.speed).coerceAtMost(1.0) * accuracy
        }

        internal fun approxDamage(weapon: Weapon, target: Ship, range: Int? = null): Double {
            val chanceToHit = chanceToHit(weapon, target, range)
            return weapon.damage * weapon.salvo * chanceToHit
        }

        private infix fun IntRange.rangePlus(other: IntRange) = (this.first + other.first)..(this.last + other.last)
        private fun List<Int>.minmax(): IntRange? = if (isEmpty()) null else min()!!..max()!!
    }

    @Suppress("unused")
    enum class Tactic(val displayName: String, val needsTarget: Boolean = true) {
        EVASIVE_MANEUVERS("Evasive Maneuvers", needsTarget = false) {
            override fun execute(battle: Battle, group: BattleGroup, target: BattleGroup?) {
                // TODO raise evade
            }
        },
        ATTACK("Attack") {
            override fun execute(battle: Battle, group: BattleGroup, target: BattleGroup?) {
                battle.attack(group, target!!)
            }
        },
        ADVANCE("Advance", needsTarget = false) {
            override fun execute(battle: Battle, group: BattleGroup, target: BattleGroup?) {
                battle.move(group, 1)
            }
        },
        RETREAT("Retreat", needsTarget = false) {
            override fun execute(battle: Battle, group: BattleGroup, target: BattleGroup?) {
                battle.move(group, -1)
            }
        },
        ;

        abstract fun execute(battle: Battle, group: BattleGroup, target: BattleGroup?)
    }

    object Serial : Serializer<Battle, Serial.Data> {
        @Serializable
        class Data(
                val zones: List<List<Int>>,
                val initiativeOrder: List<Int>,
                val initiativePoints: Map<Int, Int>
        )

        override fun save(model: Battle, refs: RefSaver): Data {
            return Data(
                    zones = model._zones.map { z -> z.map { refs.saveBattleGroupRef(it) } },
                    initiativeOrder = model.initiativeOrder.map { refs.saveBattleGroupRef(it) },
                    initiativePoints = model.initiativePoints.mapKeys { refs.saveBattleGroupRef(it.key) }
            )
        }

        override fun load(data: Data, refs: RefLoader): Battle {
            return Battle(
                    _zones = data.zones.map { z -> z.mapTo(mutableListOf()) { refs.loadBattleGroupRef(it) } }.toTypedArray(),
                    initiativeOrder = Deque(data.initiativeOrder.map { refs.loadBattleGroupRef(it) }),
                    initiativePoints = data.initiativePoints.mapKeysTo(mutableMapOf()) { refs.loadBattleGroupRef(it.key) }
            )
        }
    }
}