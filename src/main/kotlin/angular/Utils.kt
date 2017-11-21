package angular

@Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
inline fun DirectiveDefinition(def: DirectiveDefinition.() -> Unit): DirectiveDefinition =
        (js("{}") as DirectiveDefinition).apply(def)
