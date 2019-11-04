import ui.*
import ui.components.FleetTabComponent
import ui.components.GlobalDisplayPanel
import ui.components.LogTabComponent
import ui.components.StarTabComponent
import util.HttpClient
import kotlin.browser.window
import kotlin.js.Promise

fun main() {
    // Don't run main in test
    @Suppress("UnsafeCastFromDynamic")
    if (jsTypeOf(js("process")) !== "undefined" && js("process").env.NODE_ENV == "test") return

    val svc = GameService().loadOrCreate(object : HttpClient {
        override fun get(url: String): Promise<String> {
            return window.fetch(url).then { it.text() }.unsafeCast<Promise<String>>()
        }
    })
    svc.onReady += { _, _ ->
        kui.mountComponent("global", GlobalDisplayPanel(svc))
        kui.mountComponent("main", LogTabComponent(svc))
        kui.mountComponent("fleet", FleetTabComponent(svc))
        kui.mountComponent("star", StarTabComponent(svc))
    }
}
