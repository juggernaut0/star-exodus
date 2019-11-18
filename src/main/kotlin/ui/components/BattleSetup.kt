package ui.components

import game.BattleGroup
import game.Ship
import game.ShipClass
import kui.*
import ui.*

class BattleSetup(private val gameService: GameService, private val combatSimTab: CombatSimTab) : Component() {
    private val enemyGroups: MutableList<GroupInProgress> = mutableListOf()
    private var counter = 1

    private fun addGroup() {
        enemyGroups.add(GroupInProgress("Enemy #$counter"))
        counter++
        render()
    }

    private fun startCombat() {
        gameService.game.fleet.startCombat(enemyGroups.map { it.toBattleGroup() })
        combatSimTab.state = CombatView(gameService, combatSimTab)
    }

    override fun render() {
        markup().row {
            col12 {
                button(Props(
                        classes = listOf("btn", "btn-success", "btn-block"),
                        click = { startCombat() }
                )) {
                    +"Start combat"
                }
            }
            col6 {
                h4 { +"Your fleet" }
                for (group in gameService.game.fleet.groups) {
                    div(classes("card", "mb-2")) {
                        h5(classes("card-header")) { +group.name }
                        div(classes("card-body")) {
                            ul {
                                for ((cls, ships) in group.ships.groupBy { it.shipClass }) {
                                    li { +"${ships.size} x ${cls.displayName}" }
                                }
                            }
                        }
                    }
                }
            }
            col6 {
                h4 { +"Enemy Fleet" }
                for (group in enemyGroups) {
                    div(classes("card", "mb-2")) {
                        h5(classes("card-header")) { +group.name }
                        div(classes("card-body")) {
                            ul {
                                for ((cls, ships) in group.ships.groupBy { it.shipClass }) {
                                    li {
                                        +"${ships.size} x ${cls.displayName}"
                                        button(Props(
                                                classes = listOf("btn", "btn-outline-danger"),
                                                click = { group.removeShip(cls) }
                                        )) {
                                            +CLOSE
                                        }
                                    }
                                }
                            }
                            div(classes("d-flex")) {
                                select(classes("form-control", "flex-grow-1"), options = ShipClass.values().asList(), model = group::shipToAdd)
                                button(Props(
                                        classes = listOf("btn", "btn-primary"),
                                        click = { group.addShip() }
                                )) { +"Add Ship" }
                            }
                            button(Props(
                                    classes = listOf("btn", "btn-danger", "btn-block"),
                                    click = { setState { enemyGroups.remove(group) } }
                            )) { +"Remove Group" }
                        }
                    }
                }
                button(Props(
                        classes = listOf("btn", "btn-primary", "btn-block"),
                        click = { addGroup() }
                )) {
                    +"Add Group"
                }
            }
        }
    }

    private inner class GroupInProgress(var name: String, val ships: MutableList<Ship> = mutableListOf()) {
        var shipToAdd: ShipClass = ShipClass.CORVETTE

        fun addShip() {
            ships.add(Ship("Enemy ${shipToAdd.displayName}", shipToAdd))
            render()
        }

        fun removeShip(cls: ShipClass) {
            ships.indexOfFirst { it.shipClass == cls }.takeIf { it >= 0 }?.let { ships.removeAt(it) }
            render()
        }

        fun toBattleGroup(): BattleGroup {
            return BattleGroup(name, ships, enemy = true)
        }
    }
}
