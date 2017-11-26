package game

import util.WeightedList

enum class PlanetType(
        val features: WeightedList<PlanetFeature>,
        val fuelGatherAmount: Int = 0,
        val foodGatherAmount: Int = 0
) {
    ROCKY(WeightedList(
            PlanetFeature.RICH_RESOURCE_DEPOSITS to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 4,
            PlanetFeature.MASSIVE_CANYON to 3,
            PlanetFeature.CRASHED_SPACESHIP to 4,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.UNDERGROUND_CAVERNS to 5,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.NOTHING to 14
    )),
    ARID(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.MASSIVE_CANYON to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 3,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 3,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.NOTHING to 10
    ), foodGatherAmount = 2),
    DESERT(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 5,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 4,
            PlanetFeature.COLONIZED_MOON to 1,
            PlanetFeature.FEROCIOUS_FAUNA to 2,
            PlanetFeature.NOTHING to 10
    )),
    TUNDRA(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.MASSIVE_CANYON to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 2,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.NOTHING to 8
    ), foodGatherAmount = 2),
    ARCTIC(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 5,
            PlanetFeature.MASSIVE_CANYON to 4,
            PlanetFeature.COLONIZED_MOON to 1,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.NOTHING to 10
    )),
    JUNGLE(WeightedList(
            PlanetFeature.ABANDONED_CITIES to 1,
            PlanetFeature.CARNIVOROUS_PLANTS to 3,
            PlanetFeature.CRASHED_SPACESHIP to 4,
            PlanetFeature.FEROCIOUS_FAUNA to 4,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 3,
            PlanetFeature.PRIMITIVE_LOCALS to 1,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.NOTHING to 10
    ), foodGatherAmount = 4),
    CRYSTALLINE(WeightedList(
            PlanetFeature.RICH_RESOURCE_DEPOSITS to 4,
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 2,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 1,
            PlanetFeature.NOTHING to 5
    )),
    LAVA(WeightedList(
            PlanetFeature.MASSIVE_CANYON to 3,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 6,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 3,
            PlanetFeature.UNUSUAL_GASES to 3,
            PlanetFeature.NOTHING to 10
    )),
    TERRAN(WeightedList(
            PlanetFeature.ABANDONED_CITIES to 2,
            PlanetFeature.COLONIZED_MOON to 2,
            PlanetFeature.CRASHED_SPACESHIP to 1,
            PlanetFeature.FEROCIOUS_FAUNA to 1,
            PlanetFeature.HEAVILY_SETTLED to 2,
            PlanetFeature.LARGE_COLONY to 4,
            PlanetFeature.MASSIVE_SHIPYARDS to 1,
            PlanetFeature.NUCLEAR_WINTER to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 3,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 3,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.NOTHING to 10
    ), foodGatherAmount = 8),
    OCEAN(WeightedList(
            PlanetFeature.COLONIZED_MOON to 2,
            PlanetFeature.FEROCIOUS_FAUNA to 3,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 2,
            PlanetFeature.NOTHING to 10
    ), foodGatherAmount = 4),
    HYDROGEN_GIANT(WeightedList(
            PlanetFeature.COLONIZED_MOON to 5,
            PlanetFeature.JUPITER_BRAIN to 1,
            PlanetFeature.UNUSUAL_GASES to 10,
            PlanetFeature.RING_SYSTEM to 10,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 2,
            PlanetFeature.NOTHING to 26
    ), fuelGatherAmount = 5),
    HELIUM_GIANT(WeightedList(
            PlanetFeature.COLONIZED_MOON to 4,
            PlanetFeature.JUPITER_BRAIN to 1,
            PlanetFeature.UNUSUAL_GASES to 11,
            PlanetFeature.RING_SYSTEM to 10,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 2,
            PlanetFeature.NOTHING to 25
    ), fuelGatherAmount = 3),
    METHANE_GIANT(WeightedList(
            PlanetFeature.COLONIZED_MOON to 3,
            PlanetFeature.JUPITER_BRAIN to 1,
            PlanetFeature.UNUSUAL_GASES to 12,
            PlanetFeature.RING_SYSTEM to 9,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 1,
            PlanetFeature.NOTHING to 25
    ));
}

enum class PlanetFeature {
    NOTHING,
    ABANDONED_CITIES,
    PRIMITIVE_LOCALS,
    RICH_RESOURCE_DEPOSITS,
    POOR_RESOURCE_DEPOSITS,
    UNDERGROUND_CAVERNS,
    CRASHED_SPACESHIP,
    SMALL_COLONY,
    LARGE_COLONY,
    HEAVILY_SETTLED,
    CARNIVOROUS_PLANTS,
    MASSIVE_CANYON,
    RARE_ELEMENTS,
    COLONIZED_MOON,
    NUCLEAR_WINTER,
    UNUSUAL_GASES,
    MASSIVE_SHIPYARDS,
    RING_SYSTEM,
    JUPITER_BRAIN,
    FEROCIOUS_FAUNA,
    DERELICT_SHIP,
    ELECTROMAGNETIC_DISTORTIONS
}
