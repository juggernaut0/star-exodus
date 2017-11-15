package util

inline fun <T, R> List<T>.scan(init: R, folder: (R, T) -> R): List<R> {
    var state = init
    val it = iterator()
    val result = ArrayList<R>()

    result.add(state)
    while (it.hasNext()) {
        state = folder(state, it.next())
        result.add(state)
    }

    return result
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

fun String.toTitleCase(): String =
        splitToSequence('_', ' ').joinToString(separator = " ") { it.toLowerCase().capitalize() }
