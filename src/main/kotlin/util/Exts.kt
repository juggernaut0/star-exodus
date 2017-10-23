package util

import kotlin.coroutines.experimental.buildSequence

fun <T, R> Iterable<T>.scan(init: R, folder: (R, T) -> R): Sequence<R> = buildSequence {
    var state = init
    val it = iterator()

    yield(state)
    while (it.hasNext()) {
        state = folder(state, it.next())
        yield(state)
    }
}

fun <T> MutableList<T>.shuffle() {
    for (i in (size - 1) downTo 1) {
        val rand = Random.range(i+1)
        val tmp = this[i]
        this[i] = this[rand]
        this[rand] = tmp
    }
}

fun <T> List<T>.shuffled(): List<T> {
    val result = ArrayList(this)
    result.shuffle()
    return result
}
