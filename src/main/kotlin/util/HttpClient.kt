package util

import kotlin.js.Promise

interface HttpClient {
    fun get(url: String): Promise<String>
}