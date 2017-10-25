@file:JsModule("angular")
@file:JsNonModule
package angular

import kotlin.js.Promise

external class HttpService {
    fun get(url: String, config: dynamic = definedExternally): Promise<dynamic>
}