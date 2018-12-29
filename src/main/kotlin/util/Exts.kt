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

fun String.toTitleCase(): String =
        splitToSequence('_', ' ').joinToString(separator = " ") { it.toLowerCase().capitalize() }

fun <T> Sequence<T>.toTypedArray(): Array<T> {
    val arr = emptyArray<T>()
    forEach { arr.asDynamic().push(it); Unit }
    return arr
}

@Suppress("NOTHING_TO_INLINE")
inline fun Double.toPrecision(precision: Int): String = asDynamic().toPrecision(precision).unsafeCast<String>()
