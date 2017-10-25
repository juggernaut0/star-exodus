//@file:JsQualifier("angular")
@file:JsModule("angular")
@file:JsNonModule
package angular

external fun module(name: String, deps: Array<String>): Module
external fun element(element: String): dynamic

external class Module {
    fun <T: Any> controller(name: String, controller: JsClass<T>)
    fun controller(name: String, controller: Array<Any>)
    fun factory(name: String, ctor: () -> Any)
    fun factory(name: String, deps: Array<Any>)
}
