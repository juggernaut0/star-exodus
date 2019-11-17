package game

enum class Weapon(
        val displayName: String,
        val type: WeaponType,
        val damage: Int,
        val salvo: Int,
        val tracking: Int,
        val accuracy: Accuracy
) {
    AUTO_CANNON            ("AutoCannon",             WeaponType.SMALL,    3, 10, 190, Accuracy(0.7, 0.7, 0.2)),
    SMALL_LASER_TURRET     ("Small Laser Turret",     WeaponType.SMALL,   10,  2, 160, Accuracy(1.0, 1.0, 0.5, 0.1)),
    SMALL_RAILGUN_TURRET   ("Small Railgun Turret",   WeaponType.SMALL,   40,  1, 120, Accuracy(0.4, 0.8, 0.4, 0.1)),
    TORPEDO_LAUNCHER       ("Torpedo Launcher",       WeaponType.SMALL,   80,  1, 100, Accuracy(0.1, 0.4, 0.4, 0.4, 0.4, 0.1)),

    AUTO_CANNON_ARRAY      ("AutoCannon Array",       WeaponType.MEDIUM,   3, 50, 190, AUTO_CANNON.accuracy),
    FIXED_RAILGUN          ("Fixed Railgun",          WeaponType.MEDIUM, 500,  1,  40, Accuracy(0.0, 0.4, 0.9, 0.5, 0.1)),
    MEDIUM_LASER_TURRET    ("Medium Laser Turret",    WeaponType.MEDIUM,  60,  3, 100, Accuracy(0.8, 1.0, 1.0, 0.4)),
    MEDIUM_RAILGUN_TURRET  ("Medium Railgun Turret",  WeaponType.MEDIUM, 200,  2,  80, Accuracy(0.3, 0.8, 0.6, 0.4, 0.1)),
    MISSILE_LAUNCHER       ("Missile Launcher",       WeaponType.MEDIUM, 150,  2, 100, Accuracy(0.2, 0.5, 0.6, 0.5, 0.2)),

    LASER_TURRET_ARRAY     ("Laser Turret Array",     WeaponType.LARGE,   60, 15, 100, MEDIUM_LASER_TURRET.accuracy),
    MISSILE_LAUNCHER_ARRAY ("Missile Launcher Array", WeaponType.LARGE,  150, 10, 100, MISSILE_LAUNCHER.accuracy),
    SPINAL_RAILGUN         ("Spinal Railgun",         WeaponType.LARGE, 2500,  1,  20, Accuracy(0.0, 0.0, 0.0, 0.1, 0.6, 0.9, 0.6)),
}

class Accuracy private constructor(private val rangeArray: DoubleArray) {
    constructor(vararg accuracies: Double) : this(accuracies)

    init {
        require(rangeArray.size <= Battle.BATTLE_SIZE) { "Cannot have range greater than ${Battle.BATTLE_SIZE}" }
    }

    fun atDistance(distance: Int): Double = if (distance in rangeArray.indices) rangeArray[distance] else 0.0
    fun atOptimalDistance(): Double = atDistance(optimalRange)

    val minRange: Int get() = rangeArray.indexOfFirst { it > 0 }
    val maxRange: Int get() = rangeArray.indexOfLast { it > 0 }

    val optimalRange: Int get() = rangeArray.withIndex().maxBy { it.value }!!.index
}
