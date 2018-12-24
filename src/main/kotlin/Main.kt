import ui.*
import ui.components.FleetTabComponent
import ui.components.LogTabComponent
import ui.components.StarTabComponent
import util.HttpClient
import kotlin.js.Promise

fun main() {
    val svc = GameService().loadOrCreate(object : HttpClient {
        override fun get(url: String): Promise<String> {
            return jQuery.get(url)
        }
    })
    svc.onReady += { _, _ ->
        kui.mountComponent("main", LogTabComponent(svc))
        kui.mountComponent("fleet", FleetTabComponent(svc))
        kui.mountComponent("star", StarTabComponent(svc))
    }
}

private fun <T: Any> inject(vararg deps: String, cls: JsClass<T>): Array<Any> = arrayOf(*deps, cls)
private fun <T: Any> inject(vararg deps: String, factory: Function<T>): Array<Any> = arrayOf(*deps, factory)
