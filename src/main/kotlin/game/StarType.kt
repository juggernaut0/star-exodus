package game

import util.WeightedList

enum class StarType(val planetTypes: WeightedList<PlanetType>) {
    BLUE_SUPERGIANT(WeightedList(
            PlanetType.ARID to 2,
            PlanetType.DESERT to 3,
            PlanetType.ROCKY to 4,
            PlanetType.CRYSTALLINE to 1,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 3,
            PlanetType.METHANE_GIANT to 2,
            PlanetType.LAVA to 1
    )),
    BLUE_GIANT(WeightedList(
            PlanetType.ARID to 2,
            PlanetType.DESERT to 3,
            PlanetType.ROCKY to 5,
            PlanetType.CRYSTALLINE to 1,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 4,
            PlanetType.METHANE_GIANT to 2,
            PlanetType.LAVA to 2
    )),
    RED_SUPERGIANT(WeightedList(
            PlanetType.ARID to 3,
            PlanetType.DESERT to 2,
            PlanetType.ROCKY to 5,
            PlanetType.HELIUM_GIANT to 2,
            PlanetType.HYDROGEN_GIANT to 3,
            PlanetType.METHANE_GIANT to 2,
            PlanetType.LAVA to 1,
            PlanetType.TUNDRA to 1,
            PlanetType.ARCTIC to 1
    )),
    RED_GIANT(WeightedList(
            PlanetType.ARID to 3,
            PlanetType.DESERT to 2,
            PlanetType.ROCKY to 5,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 3,
            PlanetType.METHANE_GIANT to 2,
            PlanetType.TUNDRA to 1,
            PlanetType.ARCTIC to 1
    )),
    WHITE_STAR(WeightedList(
            PlanetType.ARID to 4,
            PlanetType.DESERT to 1,
            PlanetType.ROCKY to 4,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 4,
            PlanetType.METHANE_GIANT to 2,
            PlanetType.CRYSTALLINE to 1,
            PlanetType.LAVA to 1,
            PlanetType.TERRAN to 1,
            PlanetType.JUNGLE to 1,
            PlanetType.OCEAN to 1,
            PlanetType.TUNDRA to 4,
            PlanetType.ARCTIC to 1
    )),
    YELLOW_STAR(WeightedList(
            PlanetType.ARID to 3,
            PlanetType.DESERT to 1,
            PlanetType.ROCKY to 4,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 4,
            PlanetType.METHANE_GIANT to 2,
            PlanetType.CRYSTALLINE to 1,
            PlanetType.TERRAN to 1,
            PlanetType.JUNGLE to 1,
            PlanetType.OCEAN to 1,
            PlanetType.TUNDRA to 4,
            PlanetType.ARCTIC to 2
    )),
    RED_DWARF(WeightedList(
            PlanetType.ARID to 2,
            PlanetType.ROCKY to 5,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 3,
            PlanetType.TUNDRA to 4,
            PlanetType.ARCTIC to 2
    )),
    WHITE_DWARF(WeightedList(
            PlanetType.ROCKY to 6,
            PlanetType.HELIUM_GIANT to 2,
            PlanetType.HYDROGEN_GIANT to 2,
            PlanetType.TUNDRA to 3,
            PlanetType.ARCTIC to 4
    )),
    BINARY(WeightedList(
            PlanetType.ARID to 5,
            PlanetType.DESERT to 3,
            PlanetType.ROCKY to 5,
            PlanetType.HELIUM_GIANT to 2,
            PlanetType.HYDROGEN_GIANT to 4,
            PlanetType.METHANE_GIANT to 3,
            PlanetType.CRYSTALLINE to 1,
            PlanetType.LAVA to 1,
            PlanetType.TERRAN to 1,
            PlanetType.JUNGLE to 1,
            PlanetType.OCEAN to 1,
            PlanetType.TUNDRA to 2,
            PlanetType.ARCTIC to 1
    )),
    TRINARY(WeightedList(
            PlanetType.ARID to 5,
            PlanetType.DESERT to 3,
            PlanetType.ROCKY to 6,
            PlanetType.HELIUM_GIANT to 3,
            PlanetType.HYDROGEN_GIANT to 4,
            PlanetType.METHANE_GIANT to 3,
            PlanetType.LAVA to 1,
            PlanetType.JUNGLE to 1
    ));
}
