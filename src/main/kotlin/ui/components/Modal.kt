package ui.components

import jQuery
import kui.*
import ui.CLOSE

class Modal(private val id: String,
            private val title: String,
            large: Boolean = false,
            private val danger: Boolean = false,
            private val ok: (() -> Unit)? = null
) : Component() {
    private val dialogClasses = if (large) classes("modal-dialog", "modal-lg") else classes("modal-dialog")

    fun show() {
        jQuery("#$id").modal("show")
    }

    fun hide() {
        jQuery("#$id").modal("hide")
    }

    override fun render() {
        // TODO rendering while open prevents closing (wipes out jquery props on dom object)
        markup().div(Props(id, listOf("modal", "fade"))) {
            div(dialogClasses) {
                div(classes("modal-content")) {
                    div(classes("modal-header")) {
                        h5(classes("modal-title")) { +title }
                        button(Props(classes = listOf("close"), click = { hide() })) { +CLOSE }
                    }
                    div(classes("modal-body")) {
                        renderInner()
                    }
                    div(classes("modal-footer")) {
                        button(Props(classes = listOf("btn", "btn-secondary"), click = { hide() })) { +"Cancel" }
                        button(Props(classes = listOf("btn", if (danger) "btn-danger" else "btn-primary"),
                                click = { hide(); ok?.invoke() })) {
                            +"Ok"
                        }
                    }
                }
            }
        }
    }
}
