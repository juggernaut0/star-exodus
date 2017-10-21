fun main(args: Array<String>) {
    val app = angular.module("star-exodus", emptyArray())
    app.controller("star-exodus-controller", StarExodusController::class.js)
}

@Suppress("NOTHING_TO_INLINE")
private inline fun controller(deps: Array<String>, ctrl: JsClass<Any>): Array<Any> = arrayOf(*deps, ctrl)

class StarExodusController {
    var text = "Foobar"
    var i = 0

    fun increment() {
        i += 1
    }
}
