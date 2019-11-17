import ui.*
import ui.components.*
import util.HttpClient
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Promise

fun main() {
    // Don't run main in test
    @Suppress("UnsafeCastFromDynamic")
    if (jsTypeOf(js("process")) !== "undefined" && js("process").env.NODE_ENV == "test") return

    GameService().loadOrCreate(object : HttpClient {
        override fun get(url: String): Promise<String> {
            return window.fetch(url).then { it.text() }.unsafeCast<Promise<String>>()
        }
    }).onReady += { svc, _ ->
        kui.mountComponent(document.body!!, StarExodusApp(svc))
    }
}
