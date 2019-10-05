package ui.components

import kui.Component
import kui.Props
import kotlin.math.floor
import kotlin.reflect.KMutableProperty0

class ValidatedIntInput(private val range: IntRange, private val prop: KMutableProperty0<Int>, private val renderTarget: Component? = null) : Component() {
    private var value: Double
        get() = prop.get().toDouble()
        set(v) {
            val i = v.takeIf { floor(it) == it }?.toInt()
            classes = if (i != null && i in range) {
                prop.set(i)
                VALID_CLASSES
            } else {
                INVALID_CLASSES
            }
            (renderTarget ?: this).render()
        }

    private var classes = if (prop.get() in range) VALID_CLASSES else INVALID_CLASSES

    override fun render() {
        markup().inputNumber(Props(classes = classes), model = ::value)
    }

    companion object {
        private val VALID_CLASSES = listOf("form-control")
        private val INVALID_CLASSES = listOf("form-control", "is-invalid")
    }
}