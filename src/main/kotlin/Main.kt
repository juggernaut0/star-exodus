import angular.DirectiveDefinition
import ui.StarExodusController

fun main() {
    angular.module("star-exodus", emptyArray())
            .controller("star-exodus-controller",
                    controller(arrayOf("\$scope", "\$http"), StarExodusController::class.js))
            .directive("seConfirmClick") {
                DirectiveDefinition {
                    link = { scope, element, attr ->
                        val ctrl = scope.asDynamic().`_` as StarExodusController
                        val msg = (attr.seConfirmMessage as String?) ?: "Are you sure?"
                        val action = (attr.seConfirmClick as String?) ?: ""
                        element.on("click", {
                            ctrl.confirmMessage = msg
                            ctrl.confirmAction = { scope.eval(action) }
                            jQuery("#confirmModal").modal("show")
                            scope.apply()
                        })
                        Unit
                    }
                }
            }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun <T: Any> controller(deps: Array<String>, ctrl: JsClass<T>): Array<Any> = arrayOf(*deps, ctrl)
