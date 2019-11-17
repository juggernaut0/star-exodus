package ui

import game.Weapon

class WeaponView(val weapon: Weapon) {
    override fun toString(): String {
        return weapon.displayName
    }

    override fun equals(other: Any?): Boolean {
        return other is WeaponView && weapon == other.weapon
    }

    override fun hashCode(): Int {
        return 31 * weapon.hashCode()
    }
}