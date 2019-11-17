import game.*
import util.toPrecision
import kotlin.math.ceil
import kotlin.test.Test

class Weapons {
    private fun ttk(dmg: Double, target: Ship): Int {
        return ceil(target.maxHull / dmg).toInt()
    }

    @Test
    fun balance() {
        val small = Ship("Small", ShipClass.DESTROYER)
        val medium = Ship("Medium", ShipClass.HEAVY_CRUISER)
        val large = Ship("Large", ShipClass.BATTLECARRIER)

        println("Weapon              \t Range\t   Vs Small\t     Vs Med\t   Vs Large")
        println("-----------------------------------------------------------------------------")
        for (weapon in Weapon.values()) {
            println("${weapon.displayName.padEnd(20)}\t${weapon.accuracy.display()}\t${weapon vs small}\t${weapon vs medium}\t${weapon vs large}")
        }
    }

    private infix fun Weapon.vs(target: Ship): String {
        val dmg = Battle.approxDamage(this, target)
        val ttk = ttk(dmg, target)
        return "${dmg.toPrecision(4)} "+"($ttk)".padStart(5)
    }

    private fun Accuracy.display(): String {
        return "$minRange-$maxRange".padStart(6)
    }
}