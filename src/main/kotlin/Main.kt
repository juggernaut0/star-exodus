import angular.DirectiveDefinition
import angular.HttpService
import ui.GameService
import ui.StarExodusController

fun main() {
    angular.module("star-exodus", emptyArray())
            .controller("star-exodus-controller",
                    inject("\$scope", "game", cls = StarExodusController::class.js))
            .factory("game",
                    inject<GameService>("\$http") { http: HttpService ->
                        GameService().loadOrCreate(http)
                    })
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

private fun <T: Any> inject(vararg deps: String, cls: JsClass<T>): Array<Any> = arrayOf(*deps, cls)
private fun <T: Any> inject(vararg deps: String, factory: Function<T>): Array<Any> = arrayOf(*deps, factory)
