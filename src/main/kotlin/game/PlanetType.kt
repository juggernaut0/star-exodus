package game

import util.WeightedList

// TODO rebalance with new features
enum class PlanetType(
        val features: WeightedList<PlanetFeature>,
        val fuelGatherAmount: Int = 0,
        val foodGatherAmount: Int = 0
) {
    ROCKY(WeightedList(
            PlanetFeature.RICH_RESOURCE_DEPOSITS to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 4,
            PlanetFeature.MASSIVE_CANYON to 3,
            PlanetFeature.CRASHED_SPACESHIP to 2,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.UNDERGROUND_CAVERNS to 5,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.REFUGEES_ON_SURFACE to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.NOTHING to 18
    )),
    ARID(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 2,
            PlanetFeature.MASSIVE_CANYON to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 3,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 3,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.REFUGEES_ON_SURFACE to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.COLONY_DISTRESS_SIGNAL to 1,
            PlanetFeature.NOTHING to 15
    ), foodGatherAmount = 2),
    DESERT(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 4,
            PlanetFeature.COLONIZED_MOON to 1,
            PlanetFeature.FEROCIOUS_FAUNA to 2,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.NOTHING to 12
    )),
    TUNDRA(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 2,
            PlanetFeature.MASSIVE_CANYON to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 2,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.REFUGEES_ON_SURFACE to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.COLONY_DISTRESS_SIGNAL to 1,
            PlanetFeature.NOTHING to 12
    ), foodGatherAmount = 2),
    ARCTIC(WeightedList(
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.MASSIVE_CANYON to 4,
            PlanetFeature.COLONIZED_MOON to 1,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.NOTHING to 12
    )),
    JUNGLE(WeightedList(
            PlanetFeature.ABANDONED_CITIES to 1,
            PlanetFeature.CARNIVOROUS_PLANTS to 3,
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.FEROCIOUS_FAUNA to 4,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 3,
            PlanetFeature.PRIMITIVE_LOCALS to 1,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.NOTHING to 14
    ), foodGatherAmount = 4),
    CRYSTALLINE(WeightedList(
            PlanetFeature.RICH_RESOURCE_DEPOSITS to 4,
            PlanetFeature.CRASHED_SPACESHIP to 3,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 2,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 1,
            PlanetFeature.NOTHING to 5
    )),
    LAVA(WeightedList(
            PlanetFeature.MASSIVE_CANYON to 3,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 6,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 3,
            PlanetFeature.UNUSUAL_GASES to 3,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.NOTHING to 10
    )),
    TERRAN(WeightedList(
            PlanetFeature.ABANDONED_CITIES to 2,
            PlanetFeature.COLONIZED_MOON to 2,
            PlanetFeature.CRASHED_SPACESHIP to 1,
            PlanetFeature.FEROCIOUS_FAUNA to 1,
            PlanetFeature.HEAVILY_SETTLED to 3,
            PlanetFeature.LARGE_COLONY to 5,
            PlanetFeature.MASSIVE_SHIPYARDS to 1,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 3,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 3,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.REFUGEES_IN_ORBIT to 1,
            PlanetFeature.NOTHING to 10
    ), foodGatherAmount = 8),
    TOMB_WORLD(WeightedList(
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 6,
            PlanetFeature.ABANDONED_CITIES to 12,
            PlanetFeature.RARE_ELEMENTS to 2,
            PlanetFeature.UNDERGROUND_CAVERNS to 2,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 2,
            PlanetFeature.WRECKED_SHIPYARDS to 1,
            PlanetFeature.ANCIENT_SHIP to 2,
            PlanetFeature.DESTROYED_SHIP to 2,
            PlanetFeature.NOTHING to 24
    )),
    OCEAN(WeightedList(
            PlanetFeature.COLONIZED_MOON to 2,
            PlanetFeature.FEROCIOUS_FAUNA to 3,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.SMALL_COLONY to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 2,
            PlanetFeature.REFUGEES_ON_SURFACE to 1,
            PlanetFeature.NOTHING to 12
    ), foodGatherAmount = 4),
    HYDROGEN_GIANT(WeightedList(
            PlanetFeature.COLONIZED_MOON to 5,
            PlanetFeature.JUPITER_BRAIN to 1,
            PlanetFeature.UNUSUAL_GASES to 10,
            PlanetFeature.RING_SYSTEM to 10,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 2,
            PlanetFeature.NOTHING to 30
    ), fuelGatherAmount = 5),
    HELIUM_GIANT(WeightedList(
            PlanetFeature.COLONIZED_MOON to 4,
            PlanetFeature.JUPITER_BRAIN to 1,
            PlanetFeature.UNUSUAL_GASES to 11,
            PlanetFeature.RING_SYSTEM to 10,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 2,
            PlanetFeature.NOTHING to 30
    ), fuelGatherAmount = 3),
    METHANE_GIANT(WeightedList(
            PlanetFeature.COLONIZED_MOON to 3,
            PlanetFeature.JUPITER_BRAIN to 1,
            PlanetFeature.UNUSUAL_GASES to 12,
            PlanetFeature.RING_SYSTEM to 9,
            PlanetFeature.ANCIENT_SHIP to 1,
            PlanetFeature.DESTROYED_SHIP to 1,
            PlanetFeature.ABANDONDED_SHIP to 1,
            PlanetFeature.ELECTROMAGNETIC_DISTORTIONS to 1,
            PlanetFeature.NOTHING to 30
    ));
}
