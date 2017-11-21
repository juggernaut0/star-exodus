@file:JsModule("angular")
@file:JsNonModule
package angular

external interface DirectiveDefinition {
    var link: (Scope, dynamic, dynamic) -> Unit
}
