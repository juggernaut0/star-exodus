package ui.components

import jQuery
import kui.Component
import kui.Props

class Collapse(private val id: String,
               private val body: Component,
               private var show: Boolean = false) : Component() {
    fun show() {
        jQuery("#$id").collapse("show")
        show = true
    }

    fun hide() {
        jQuery("#$id").collapse("hide")
        show = false
    }

    override fun render() {
        markup().div(Props(id, if (show) listOf("collapse", "show") else listOf("collapse"))) {
            component(body)
        }
    }
}