package game

class ShipClass(
        val name: String,
        val maxCrew: Int,
        val minCrew: Int,
        val maxHull: Int,
        val speed: Int,
        val cargoCapacity: Int,
        val upgradeSlots: Int,
        smSlots: Int, mdSlots: Int, lgSlots: Int,
        val hanger: Int
) {
    val weaponSlots = mapOf(WeaponType.SMALL to smSlots, WeaponType.MEDIUM to mdSlots, WeaponType.LARGE to lgSlots)
}