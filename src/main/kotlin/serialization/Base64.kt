package serialization

import kotlin.browser.window

object Base64 {
    fun encode(bytes: ByteArray): String = window.btoa(bytes.map { it.toChar() }.joinToString())

    fun decode(string: String): ByteArray = window.atob(string).map { it.toByte() }.toByteArray()
}