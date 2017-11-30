package util

open class Event<TSender : EventEmitter<TSender>, TArgs : Any> {

    private val handlers: MutableList<(TSender, TArgs) -> Unit> = mutableListOf()

    open fun bind(emitter: TSender): Event<TSender, TArgs> {
        emitter.register(this, ::invoke)
        return this
    }

    protected fun invoke(sender: TSender, args: TArgs) = handlers.forEach { it(sender, args) }

    open operator fun plusAssign(handler: (TSender, TArgs) -> Unit) { handlers.add(handler) }
    open operator fun minusAssign(handler: (TSender, TArgs) -> Unit) { handlers.remove(handler) }
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

    fun <TArgs : Any> register(event: Event<TSender, TArgs>, invoker: (TSender, TArgs) -> Unit) {
        events[event] = invoker
    }

    protected operator fun <TArgs : Any> Event<TSender, TArgs>.invoke(args: TArgs) {
        val invoker = events[this] ?: return
        // Gotta trust
        invoker.unsafeCast<(TSender, TArgs) -> Unit>().invoke(this@EventEmitter.unsafeCast<TSender>(), args)
    }
}
