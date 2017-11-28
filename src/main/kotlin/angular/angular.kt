@file:JsModule("angular")
@file:JsNonModule
package angular

external fun module(name: String, deps: Array<String>): Module
external fun element(element: String): dynamic

external class Module {
    fun <T: Any> controller(name: String, controller: JsClass<T>): Module
    fun controller(name: String, controller: Array<Any>): Module
    fun directive(name: String, factory: () -> DirectiveDefinition): Module
    fun directive(name: String, factory: Array<Any>): Module
    fun <T> factory(name: String, ctor: () -> T): Module
    fun factory(name: String, deps: Array<Any>): Module
}

external class Scope {
    @JsName("\$apply")
    fun apply(expr: String? = definedExternally)

    @JsName("\$eval")
    fun eval(expr: String)
}
