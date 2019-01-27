package game

import util.Random
import util.WeightedList

enum class SystemArrivalEvent(private val genWeight: Int) {
    NOTHING(30),
    ATTACKED(12) {
        override fun execute(fleet: Fleet) {
            fleet.startCombat() // TODO strength
        }
    },
    PIRATES(6) {
        override fun canGen(fleet: Fleet): Boolean {
            return !fleet.currentLocation.isSettled()
        }

        override fun execute(fleet: Fleet) {
            fleet.startCombat() // TODO strength
        }
    },
    BANDITS(6) {
        override fun canGen(fleet: Fleet): Boolean {
            return !fleet.currentLocation.isSettled()
        }

        override fun execute(fleet: Fleet) {
            fleet.setHailed(BlockedState.Hailed())
        }
    },
    HOSTILE_CIVILIZATION(2) {
        override fun canGen(fleet: Fleet): Boolean {
            return fleet.currentLocation.planets.any { PlanetFeature.HEAVILY_SETTLED in it.features }
        }

        override fun execute(fleet: Fleet) {
            fleet.startTimer(this, Random.range(2, 11))
        }

        override fun onTimerFinished(fleet: Fleet) {
            fleet.currentLocation.threatRate = 0.1
            fleet.currentLocation.threat = 0.5
            fleet.startCombat() // TODO strength
        }
    },
    DISTRESS_SIGNAL(10) {
        override fun canGen(fleet: Fleet): Boolean {
            return fleet.currentLocation.let {
                !it.hasHabitablePlanet() && it.anyPlanetHasFeature(
                        PlanetFeature.REFUGEES_ON_SURFACE,
                        PlanetFeature.REFUGEES_IN_ORBIT,
                        PlanetFeature.ABANDONDED_SHIP,
                        PlanetFeature.DESTROYED_SHIP
                )
            }
        }
    }
    ;

    protected open fun canGen(fleet: Fleet) = true
    protected open fun execute(fleet: Fleet) { /*empty*/ }
    internal open fun onTimerFinished(fleet: Fleet) { /*empty*/ }

    companion object {
        fun generateAndExecute(fleet: Fleet): SystemArrivalEvent {
            val event = generate(fleet)
            console.log("generated event: ", event)
            event.execute(fleet)
            return event
        }

        private fun generate(fleet: Fleet): SystemArrivalEvent {
            val items = mutableMapOf<SystemArrivalEvent, Int>()
            for (event in values()) {
                if (event.canGen(fleet)) {
                    items[event] = event.genWeight
                }
            }
            return Random.choice(WeightedList(items))
        }

        private fun StarSystem.anyPlanetHasFeature(vararg features: PlanetFeature): Boolean {
            val s = features.toSet()
            return planets.any { p -> p.features.any { it in s } }
        }

        private fun StarSystem.isSettled() = anyPlanetHasFeature(
                PlanetFeature.COLONIZED_MOON,
                PlanetFeature.SMALL_COLONY,
                PlanetFeature.LARGE_COLONY,
                PlanetFeature.HEAVILY_SETTLED)

        private val habitable = setOf(
                PlanetType.ARID,
                PlanetType.TUNDRA,
                PlanetType.JUNGLE,
                PlanetType.TERRAN,
                PlanetType.OCEAN
        )
        private fun StarSystem.hasHabitablePlanet() = planets.any { it.type in habitable }
    }
}
