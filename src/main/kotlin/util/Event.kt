package util

class Event<out TSender : EventEmitter, out TArgs>(emitter: TSender) {
    private val handlers: MutableList<(TSender, TArgs) -> Unit> = mutableListOf()

    init {
        emitter.register(this, this::invoke)
    }

    operator fun plusAssign(handler: (TSender, TArgs) -> Unit) { handlers.add(handler) }
    operator fun minusAssign(handler: (TSender, TArgs) -> Unit) { handlers.remove(handler) }

    private fun invoke(sender: TSender, args: TArgs) {
        handlers.forEach { it(sender, args) }
    }
}

abstract class EventEmitter {
    private val events: MutableMap<Event<*, *>, Function<Unit>> = mutableMapOf()

    fun <TSender : EventEmitter, TArgs> register(event: Event<TSender, TArgs>, invoker: (TSender, TArgs) -> Unit) {
        events[event] = invoker
    }

    protected fun <TSender : EventEmitter, TArgs> invoke(event: Event<TSender, TArgs>, args: TArgs) {
        // Gotta trust
        events[event]?.unsafeCast<(TSender, TArgs) -> Unit>()?.invoke(this.unsafeCast<TSender>(), args)
    }
}
