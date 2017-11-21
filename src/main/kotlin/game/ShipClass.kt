package game

enum class ShipClass(
        val displayName: String,
        val maxCrew: Int,
        val minCrew: Int,
        val maxHull: Int,
        val speed: Int,
        val cargoCapacity: Int,
        val upgradeSlots: Int,
        smSlots: Int = 0, mdSlots: Int = 0, lgSlots: Int = 0,
        val hanger: Int = 0,
        val foodProduction: Int = 0
) {
    // civilian
    SMALL_PASSENGER_CARRIER( "Small Passenger Carrier",  50,   2,  200,  90,   80, 1),
    MEDIUM_PASSENGER_CARRIER("Medium Passenger Carrier", 90,   3,  300,  85,  160, 1),
    LARGE_PASSENGER_CARRIER( "Large Passenger Carrier", 150,   5,  360,  80,  240, 2),
    HUGE_PASSENGER_CARRIER(  "Huge Passenger Carrier",  250,   8,  400,  75,  320, 3),
    CRUISE_LINER(            "Cruise Liner",            400,  12,  500,  70,  480, 3),
    DREAM_LINER(             "Dream Liner",             600,  16,  600,  70,  800, 4),
    SMALL_COLONY_SHIP(       "Small Colony Ship",       900,  25, 1000,  80, 1800, 3, hanger =   5),
    LARGE_COLONY_SHIP(       "Large Colony Ship",      1500,  50, 1200,  70, 3200, 4, hanger =  10),
    LIVESHIP(                "Liveship",               2500, 100, 1800,  55, 4800, 5, hanger =  50, foodProduction = 25),
    CITYSHIP(                "Cityship",               5000, 250, 3600,  40, 7200, 6, hanger = 100, foodProduction = 55),
    SMALL_FREIGHT_CARRIER(   "Small Freight Carrier",    25,   3,  240,  90,  160, 1),
    MEDIUM_FREIGHT_CARRIER(  "Medium Freight Carrier",   45,   6,  300,  80,  320, 1),
    LARGE_FREIGHT_CARRIER(   "Large Freight Carrier",    80,  10,  360,  70,  560, 2),
    HUGE_FREIGHT_CARRIER(    "Huge Freight Carrier",    120,  20,  440,  60,  800, 2),
    SUPER_FREIGHT_CARRIER(   "Super Freight Carrier",   150,  35,  500,  55, 1120, 3),
    REFINERY_SHIP(           "Refinery Ship",           100,  40,  460,  80,  480, 2),
    FUEL_TANKER(             "Fuel Tanker",              60,  10,  400,  70, 1280, 2),
    MOBILE_DRY_DOCK(         "Mobile Dry-Dock",         160,  80,  500,  65,  400, 2, hanger = 10),
    MINING_SHIP(             "Mining Ship",              60,  30,  400,  75,  720, 2),
    // military
    CORVETTE(                "Corvette",                 15,   5,  300, 180,   40, 0, smSlots = 2),
    SCOUT(                   "Scout",                    10,   5,  300, 220,   48, 1, smSlots = 2),
    DESTROYER(               "Destroyer",                35,  10,  440, 150,  100, 1, smSlots = 2, mdSlots = 1),
    TROOP_CARRIER(           "Troop Carrier",           100,  10,  500, 120,   60, 1, smSlots = 2),
    FRIGATE(                 "Frigate",                  75,  25,  700, 110,  160, 2, smSlots = 3, mdSlots = 1),
    CRUISER(                 "Cruiser",                 150,  60,  900, 110,  160, 2, smSlots = 4, mdSlots = 2,              hanger =   2),
    HEAVY_CRUISER(           "Heavy Cruiser",           300, 125, 1200, 100,  200, 3, smSlots = 4, mdSlots = 1, lgSlots = 1, hanger =   5),
    CARRIER(                 "Carrier",                 250, 125, 1100, 100,  240, 2, smSlots = 1, mdSlots = 1,              hanger =  50),
    BATTLESHIP(              "Battleship",              500, 250, 1600,  90,  280, 3, smSlots = 4, mdSlots = 2, lgSlots = 1, hanger =  20),
    DREADNOUGHT(             "Dreadnought",             800, 400, 2400,  80,  400, 4, smSlots = 5, mdSlots = 3, lgSlots = 1, hanger =  30),
    FLEET_CARRIER(           "Fleet Carrier",           750, 400, 1800,  75,  480, 4, smSlots = 2, mdSlots = 2,              hanger = 120),
    TITAN(                   "Titan",                  1100, 600, 3600,  60,  600, 5, smSlots = 5, mdSlots = 4, lgSlots = 2, hanger =  50),
    BATTLECARRIER(           "BattleCarrier",          1500, 800, 4800,  60,  640, 5, smSlots = 4, mdSlots = 3, lgSlots = 2, hanger = 100),
    // strike craft
    INTERCEPTOR(             "Interceptor",               1,   1,  100,   0,    0, 0, smSlots = 1, hanger = -1),
    BOMBER(                  "Bomber",                    1,   1,  120,   0,    8, 0, mdSlots = 1, hanger = -1),
    RAPTOR(                  "Raptor",                    3,   1,  120,  30,   20, 1, smSlots = 1, hanger = -1);

    val weaponSlots = mapOf(WeaponType.SMALL to smSlots, WeaponType.MEDIUM to mdSlots, WeaponType.LARGE to lgSlots)
}
