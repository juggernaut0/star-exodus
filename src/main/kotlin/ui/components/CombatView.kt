package ui.components

import game.BlockedState
import game.Battle
import game.BattleGroup
import kui.*
import ui.*
import util.Event

class CombatView(private val gameService: GameService) : Component() {
    private val battle: Battle get() = (gameService.game.fleet.blockedState as BlockedState.Combat).battle
    private var hoveredGroup: BattleGroup? by renderOnSet(null)
    private var selectedTactic: TacticView by renderOnSet(TacticView(Battle.Tactic.EVASIVE_MANEUVERS))
    private var selectedTarget: BattleGroup? by renderOnSet(null)
    private val combatLog: MutableList<String> = mutableListOf()

    init {
        battle.onCombatEvent.plusAssign(Event.Handler(this::class) { _, msg ->
            combatLog.add(msg)
        })
    }

    private fun executeOrder/*66*/() {
        combatLog.clear()
        battle.executeTurn(selectedTactic.tactic, selectedTarget)
        selectedTactic = TacticView(Battle.Tactic.EVASIVE_MANEUVERS)
        selectedTarget = null
    }

    private fun MarkupBuilder.groupCard(group: BattleGroup) {
        div(Props(
                classes = listOf("card", "battle-group", if (group.enemy) "battle-group-enemy" else "battle-group-ally"),
                mouseenter = { hoveredGroup = group },
                mouseleave = { hoveredGroup = null },
                click = { selectedTarget = if (selectedTarget == group) null else group }
        )) {
            div(classes("card-body")) {
                div(classes("d-inline-block", "w-100")) {
                    +group.name
                }
                small(classes("d-inline-block", "w-100")) {
                    val count = group.ships.size
                    +"$count ${pluralize("ship", count)}"
                }
                small(classes("d-inline-block", "w-100")) {
                    val hp = group.ships.sumBy { it.hullPoints }
                    val maxHp = group.ships.sumBy { it.maxHull }
                    +"HP: $hp/$maxHp"
                }
            }
        }
    }

    private fun MarkupBuilder.groupDetails(group: BattleGroup) {
        div(classes("card-body")) {
            h4 { +group.name }
            if (selectedTactic.tactic == Battle.Tactic.ATTACK && group != battle.currentGroup) {
                val dmg = battle.estimateDamageAgainst(group)
                if (dmg.first == dmg.last) {
                    p(if (dmg.first == 0) classes("text-danger") else Props.empty) { +"Estimated Damage: ${dmg.first}" }
                } else {
                    p { +"Estimated Damage: ${dmg.first} to ${dmg.last}" }
                }
            }
            ul {
                for (ship in group.ships.map { it.toView() }) {
                    li {
                        +"${ship.name} - ${ship.shipClass} - ${ship.hull}"
                        ul {
                            for ((weapon, count) in ship.ship.weapons.groupBy { it }.mapValues { it.value.size }) {
                                li { +"$count x ${weapon.displayName}" }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun render() {
        val battle = battle
        markup().row {
            if (battle.finished) {
                p { +"The battle is finished." }
                // TODO stats?
                button(Props(
                        classes = listOf("btn", "btn-primary"),
                        click = { gameService.viewState = GameService.ViewState.MAIN }
                )) { +"Return to Fleet" }
            } else {
                col6 {
                    div(classes("list-group")) {
                        for (i in 0 until Battle.BATTLE_SIZE) {
                            div(classes("list-group-item", "battle-zone")) {
                                for (group in battle.zones[i]) {
                                    groupCard(group)
                                }
                            }
                        }
                    }
                }
                col6 {
                    div(classes("card", "mb-2")) {
                        h5(classes("card-header")) { +"Awaiting orders" }
                        groupDetails(battle.currentGroup)
                        div(classes("card-footer")) {
                            val disabled = selectedTactic.tactic.needsTarget && selectedTarget == null
                            div(classes("d-flex")) {
                                select(classes("form-control", "mr-1"),
                                        options = Battle.Tactic.values().map { TacticView(it) },
                                        model = ::selectedTactic)
                                button(Props(
                                        classes = listOf("btn", "btn-warning"),
                                        disabled = disabled,
                                        click = { executeOrder() }
                                )) { +"Execute" }
                            }
                            if (disabled) {
                                span(classes("text-danger")) { +"You must select a target." }
                            }
                        }
                    }
                    (hoveredGroup ?: selectedTarget)?.let {
                        div(classes("card")) {
                            h5(classes("card-header")) { +"Target" }
                            groupDetails(it)
                        }
                    }
                    if (combatLog.isNotEmpty()) {
                        div(classes("list-group")) {
                            for (item in combatLog) {
                                div(classes("list-group-item")) {
                                    +item
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class TacticView(val tactic: Battle.Tactic) {
        override fun toString(): String {
            return tactic.displayName
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class.js != other::class.js) return false

            other as TacticView

            if (tactic != other.tactic) return false

            return true
        }

        override fun hashCode(): Int {
            return tactic.hashCode()
        }
    }
}
