package util

class Event<TSender, TArgs> {
    private val handlers: MutableList<(TSender, TArgs) -> Unit> = mutableListOf()

    operator fun plusAssign(handler: (TSender, TArgs) -> Unit) { handlers.add(handler) }
    operator fun minusAssign(handler: (TSender, TArgs) -> Unit) { handlers.remove(handler) }

    operator fun invoke(sender: TSender, args: TArgs) {
        handlers.forEach { it(sender, args) }
    }
}
