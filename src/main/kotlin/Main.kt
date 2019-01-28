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
    if (window.asDynamic().test != undefined) return

    val svc = GameService().loadOrCreate(object : HttpClient {
        override fun get(url: String): Promise<String> {
            return jQuery.get(url)
        }
    })
    svc.onReady += { _, _ ->
        kui.mountComponent("global", GlobalDisplayPanel(svc))
        kui.mountComponent("main", LogTabComponent(svc))
        kui.mountComponent("fleet", FleetTabComponent(svc))
        kui.mountComponent("star", StarTabComponent(svc))
    }
}
