import kotlin.js.Promise

external fun jQuery(selector: String): dynamic

external object jQuery {
    fun get(url: String): Promise<dynamic>
}