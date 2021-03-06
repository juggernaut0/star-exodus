package util

open class Event<TSender : EventEmitter<TSender>, TArgs : Any> {
    private val handlers: MutableMap<Any, (TSender, TArgs) -> Unit> = mutableMapOf()

    internal open fun bind(emitter: TSender): Event<TSender, TArgs> {
        emitter.register(this, ::invoke)
        return this
    }

    protected fun invoke(sender: TSender, args: TArgs) = handlers.values.forEach { it(sender, args) }

    open operator fun plusAssign(handler: (TSender, TArgs) -> Unit) { handlers[Any()] = handler }
    open operator fun plusAssign(handler: Handler<TSender, TArgs>) { handlers[handler.key] = handler.fn }
    open operator fun minusAssign(key: Any) { handlers.remove(key) }

    class Handler<TSender : EventEmitter<TSender>, TArgs : Any>(val key: Any, val fn: (TSender, TArgs) -> Unit)
}

class OneTimeEvent<TSender : EventEmitter<TSender>, TArgs : Any> : Event<TSender, TArgs>() {
    private var invoked = false
    private lateinit var sender: TSender
    private lateinit var args: TArgs

    override fun bind(emitter: TSender): Event<TSender, TArgs> {
        emitter.register(this) { sender, args ->
            if (invoked) throw IllegalStateException("OneTimeEvent has already been invoked")
            invoked = true
            this.sender = sender
            this.args = args
            invoke(sender, args)
        }
        return this
    }

    override operator fun plusAssign(handler: (TSender, TArgs) -> Unit) {
        if (invoked) {
            handler(sender, args)
        } else {
            super.plusAssign(handler)
        }
    }
}

abstract class EventEmitter<TSender : EventEmitter<TSender>> {
    private val events: MutableMap<Event<*, *>, Function<Unit>> = mutableMapOf()

    internal fun <TArgs : Any> register(event: Event<TSender, TArgs>, invoker: (TSender, TArgs) -> Unit) {
        events[event] = invoker
    }

    protected operator fun <TArgs : Any> Event<TSender, TArgs>.invoke(args: TArgs) {
        val invoker = events[this] ?: return
        // Gotta trust
        invoker.unsafeCast<(TSender, TArgs) -> Unit>().invoke(this@EventEmitter.unsafeCast<TSender>(), args)
    }
}

fun <TSender : EventEmitter<TSender>, TArgs : Any> TSender.event() = Event<TSender, TArgs>().bind(this)
fun <TSender : EventEmitter<TSender>, TArgs : Any> TSender.oneTimeEvent() = OneTimeEvent<TSender, TArgs>().bind(this)
