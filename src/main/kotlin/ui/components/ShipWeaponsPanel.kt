package ui.components

import game.Ship
import game.Weapon
import game.WeaponType
import kui.*
import ui.*
import util.toTitleCase
import kotlin.math.roundToInt

class ShipWeaponsPanel(private val gameService: GameService, ship: ShipView) : Component() {
    private val ship: Ship = ship.ship

    private var selectedWeapon: Weapon? by renderOnSet(null)

    private val modal = EquipWeaponModal()

    private fun equip(selectedWeapon: Weapon?) {
        if (selectedWeapon != null) {
            gameService.game.fleet.equipWeapon(ship, selectedWeapon)
        }
        render()
    }

    private fun MarkupBuilder.weaponColumn(type: WeaponType) {
        col4 {
            h6 { +type.name.toTitleCase() }
            val weapons = ship.weapons.filter { it.type == type }
            for (weapon in weapons) {
                weaponPanel(weapon)
            }
            if (weapons.size < ship.shipClass.weaponSlots(type)) {
                button(Props(
                        classes = listOf("btn", "btn-primary", "btn-block"),
                        click = { modal.show(gameService.game.fleet.spareWeapons(type).sorted()).then { equip(it) } }
                )) {
                    +"Equip"
                }
            }
        }
    }

    private fun MarkupBuilder.weaponPanel(weapon: Weapon) {
        div(classes("btn-group", "mb-1", "d-flex")) {
            button(Props(
                    classes = listOf("btn", if (selectedWeapon == weapon) "btn-secondary" else "btn-outline-secondary", "flex-grow-1"),
                    click = { selectedWeapon = weapon }
            )) {
                +weapon.displayName
            }
            button(Props(
                    classes = listOf("btn", "btn-outline-danger"),
                    click = { gameService.game.fleet.unequipWeapon(ship, weapon); render() }
            )) {
                +CLOSE
            }
        }
    }

    override fun render() {
        markup().div(classes("card", "mt-2")) {
            div(classes("card-body")) {
                h5(classes("card-title")) { +"Weapons" }
                row {
                    weaponColumn(WeaponType.SMALL)
                    weaponColumn(WeaponType.MEDIUM)
                    weaponColumn(WeaponType.LARGE)
                }

                val weapon = selectedWeapon
                if (weapon != null) {
                    div {
                        row {
                            col4 { +"Damage: ${weapon.damage}" }
                            col4 { +"Salvo: ${weapon.salvo}" }
                            col4 { +"Tracking: ${weapon.tracking}" }
                        }
                        h6 { +"Accuracy Profile:" }
                        div(classes("accuracy-box")) {
                            for (d in 0..6) {
                                val h = (weapon.accuracy.atDistance(d) * 100).roundToInt()
                                div(classes("accuracy-bar-label")) {
                                    div(Props(classes = listOf("accuracy-bar"), attrs = mapOf("style" to "height: $h%;"))) { }
                                    span(classes("position-absolute", "w-100", "text-center")) { +"$h%" }
                                }
                            }
                        }
                    }
                }

                component(modal)
            }
        }
    }
}
