package game

import kotlinx.serialization.Serializable
import serialization.RefLoader
import serialization.RefSaver
import serialization.Serializer
import util.Random

abstract class StarSequence(start: StarSystem, next: List<StarTarget>) {
    var current: StarSystem = start
        private set
    var next: List<StarTarget> = next
        private set

    protected abstract fun generateNext(): List<StarTarget>

    internal fun advance(i: Int) {
        if (i !in next.indices) throw IllegalArgumentException("i must be in next.indices")
        current = next[i].star
        next = generateNext()
    }

    class StarTarget(val star: StarSystem, val distance: Int)
}

class MainSequence(start: StarSystem, next: List<StarTarget>) : StarSequence(start, next) {
    override fun generateNext() = Companion.generateNext()

    companion object {
        private fun generateNext(): List<StarTarget> {
            return List(Random.range(2, 5)) { StarTarget(StarSystem(), Random.range(80, 400)) }
        }

        operator fun invoke(): MainSequence {
            return MainSequence(StarSystem(), generateNext())
        }
    }

    object Serial : Serializer<MainSequence, Serial.Data> {
        @Serializable
        class Data(val current: StarSystem.Serial.Data, val next: List<TargetData>)
        @Serializable
        class TargetData(val star: StarSystem.Serial.Data, val distance: Int)

        override fun save(model: MainSequence, refs: RefSaver): Data {
            return Data(
                    StarSystem.Serial.save(model.current, refs),
                    model.next.map { TargetData(StarSystem.Serial.save(it.star, refs), it.distance) }
            )
        }

        override fun load(data: Data, refs: RefLoader): MainSequence {
            return MainSequence(
                    StarSystem.Serial.load(data.current, refs),
                    data.next.map { StarTarget(StarSystem.Serial.load(it.star, refs), it.distance) }
            )
        }
    }
}
