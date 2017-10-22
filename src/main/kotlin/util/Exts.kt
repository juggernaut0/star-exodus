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