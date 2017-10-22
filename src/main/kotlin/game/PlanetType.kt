package game

import util.WeightedList

enum class PlanetType(val features: WeightedList<PlanetFeature>) {
    ROCKY(WeightedList(
            PlanetFeature.RICH_RESOURCE_DEPOSITS to 2,
            PlanetFeature.POOR_RESOURCE_DEPOSITS to 4,
            PlanetFeature.MASSIVE_CANYON to 3,
            PlanetFeature.CRASHED_SPACESHIP to 4,
            PlanetFeature.RARE_ELEMENTS to 1,
            PlanetFeature.UNDERGROUND_CAVERNS to 5,
            PlanetFeature.DERELICT_SHIP to 1,
            PlanetFeature.NOTHING to 14
    ))
}

enum class PlanetFeature {
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
    ELECTROMAGNETIC_DISTORTIONS,
    NOTHING
}
