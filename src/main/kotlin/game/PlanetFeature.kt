package game

enum class PlanetFeature(val description: String, val tradeCapacity: Int = 0) {
    NOTHING(               "Nothing of value."),
    ABANDONED_CITIES(      "Destroyed husks of cities dot the landscape."),
    PRIMITIVE_LOCALS(      "A low-technology civilization inhabits this planet."),
    RICH_RESOURCE_DEPOSITS("Rich veins of metal and ore have been detected."),
    POOR_RESOURCE_DEPOSITS("Sparse veins of metal and ore have been detected."),
    UNDERGROUND_CAVERNS(   "Subsurface scan have revealed an extensive cave network beneath the surface."),
    CRASHED_SPACESHIP(     "Scattered debris surrounds the wreck of a starship on the surface."),
    SMALL_COLONY(          "A small but prosperous alien colony is present on this planet.",        tradeCapacity = 1500),
    LARGE_COLONY(          "A large alien colony is present on this planet, and is open to trade.", tradeCapacity = 4000),
    HEAVILY_SETTLED(       "This is home to a massive civilzation spanning the planet.",            tradeCapacity = 10000),
    CARNIVOROUS_PLANTS(    "The flora on this planet has acquired a taste for meat!"),
    MASSIVE_CANYON(        "An enormous canyon stretches across the landscape."),
    RARE_ELEMENTS(         "Traces of rare and valuable elements have been detected."),
    COLONIZED_MOON(        "One of this planet's moons is home to a small outpost.",                tradeCapacity = 750),
    NUCLEAR_WINTER(        "This planet is a radioactive wastland, shrouded in artificial winter."),
    UNUSUAL_GASES(         "This planet's atmosphere contains a peculiar mix of gases."),
    MASSIVE_SHIPYARDS(     "A massive ship-building complex is present in orbit."),
    RING_SYSTEM(           "This planet is host to a set of beautiful rings of ice and rock."),
    JUPITER_BRAIN(         "There seems to be a massive intelligence lurking within this planet..."),
    FEROCIOUS_FAUNA(       "The wildlife on this planet is especially aggressive and dangerous."),
    DERELICT_SHIP(         "The ancient hulk of a starship lurks above this planet."),
    ELECTROMAGNETIC_DISTORTIONS("Sensors are being distorted by some phenomenon around this planet.")
}
