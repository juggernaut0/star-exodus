import ui.StarExodusController

fun main() {
    val app = angular.module("star-exodus", emptyArray())
    app.controller("star-exodus-controller", controller(arrayOf("\$scope", "\$http"), StarExodusController::class.js))
}

@Suppress("NOTHING_TO_INLINE")
private inline fun <T: Any> controller(deps: Array<String>, ctrl: JsClass<T>): Array<Any> = arrayOf(*deps, ctrl)
