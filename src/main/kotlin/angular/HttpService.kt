@file:JsModule("angular")
@file:JsNonModule
package angular

import kotlin.js.Promise

external class HttpService {
    fun <T> get(url: String, config: dynamic = definedExternally): Promise<HttpResponse<T>>
}

external class HttpResponse<out T> {
    val data: T
    val status: Int
    val statusText: String
    val xhrStatus: String
}
