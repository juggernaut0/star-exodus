package ui.components

import game.Weapon
import kui.*
import kotlin.js.Promise

class EquipWeaponModal : Component() {
    private var options: List<WeaponView> = emptyList()
    private var selectedWeapon: WeaponView? = null
    private lateinit var resolve: (Weapon?) -> Unit
    private val modal = Modal("equipWeaponModal", "Equip Weapon", ok = { resolve(selectedWeapon?.weapon) })

    fun show(options: List<Weapon>): Promise<Weapon?> {
        this.options = options.map { WeaponView(it) }
        render()
        modal.show()
        return Promise { resolve, _ -> this.resolve = resolve }
    }

    override fun render() {
        markup().component(modal) {
            slot(Unit) {
                select(classes("form-control"), options = options, model = ::selectedWeapon)
            }
        }
    }

    private class WeaponView(val weapon: Weapon) {
        override fun toString(): String {
            return weapon.displayName
        }
    }
}
