package util

class Event<TSender : EventEmitter<TSender>, out TArgs>(emitter: TSender) {
    private val handlers: MutableList<(TSender, TArgs) -> Unit> = mutableListOf()

    init {
        emitter.register(this) { sender, args -> handlers.forEach { it(sender, args) } }
    }

    operator fun plusAssign(handler: (TSender, TArgs) -> Unit) { handlers.add(handler) }
    operator fun minusAssign(handler: (TSender, TArgs) -> Unit) { handlers.remove(handler) }
}

abstract class EventEmitter<TSender : EventEmitter<TSender>> {
    private val events: MutableMap<Event<*, *>, Function<Unit>> = mutableMapOf()

    fun <TArgs> register(event: Event<TSender, TArgs>, invoker: (TSender, TArgs) -> Unit) {
        events[event] = invoker
    }

    protected operator fun <TArgs> Event<TSender, TArgs>.invoke(args: TArgs) {
        val invoker = events[this] ?: return
        // Gotta trust
        invoker.unsafeCast<(TSender, TArgs) -> Unit>().invoke(this@EventEmitter.unsafeCast<TSender>(), args)
    }
}
