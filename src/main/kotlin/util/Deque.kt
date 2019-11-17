package util

class Deque<T>() : AbstractMutableCollection<T>() {
    constructor(itbl: Iterable<T>) : this() {
        for (it in itbl) {
            pushBack(it)
        }
    }

    private class Node<T>(val data: T, var prev : Node<T>? = null, var next: Node<T>? = null)

    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    override var size: Int = 0
        private set

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        private var current: Node<T>? = null
        private var next: Node<T>? = head

        override fun hasNext(): Boolean = next != null

        override fun next(): T {
            val current = checkNotNull(next)
            next = current.next
            this.current = current
            return current.data
        }

        override fun remove() {
            val current = checkNotNull(current)
            when {
                current.prev == null -> popFront()
                current.next == null -> popBack()
                else -> {
                    current.prev!!.next = current.next
                    current.next!!.prev = current.prev
                    size--
                }
            }
        }
    }

    operator fun get(index: Int): T {
        if (index < 0) throw IndexOutOfBoundsException("negative index")
        val current = head ?: throw IndexOutOfBoundsException("empty queue")
        return (0 until index).fold(current) { node, _ -> node.next ?: throw IndexOutOfBoundsException(index.toString()) }.data
    }

    override fun add(element: T): Boolean {
        pushBack(element)
        return true
    }

    fun pushFront(elem: T) {
        val node = Node(elem, next = head)
        head?.prev = node
        head = node
        if (tail == null) {
            tail = node
        }
        size += 1
    }

    fun pushBack(elem: T) {
        val node = Node(elem, prev = tail)
        tail?.next = node
        tail = node
        if (head == null) {
            head = node
        }
        size += 1
    }

    fun popFront(): T {
        val h = head ?: throw IndexOutOfBoundsException("empty queue")
        head = h.next
        head?.prev = null
        if (head == null) {
            tail = null
        }
        size -= 1
        return h.data
    }

    fun popBack(): T {
        val t = tail ?: throw IndexOutOfBoundsException("empty queue")
        tail = t.prev
        tail?.next = null
        if (tail == null) {
            head = null
        }
        size -= 1
        return t.data
    }

    fun peekFront(): T = head?.data ?: throw IndexOutOfBoundsException("empty queue")

    fun peekBack(): T = tail?.data ?: throw IndexOutOfBoundsException("empty queue")

    fun asReversed(): Iterable<T> = object : Iterable<T> {
        override fun iterator(): Iterator<T> = object : Iterator<T> {
            private var next: Node<T>? = tail

            override fun hasNext(): Boolean = next != null

            override fun next(): T {
                val current = next!!
                next = current.prev
                return current.data
            }
        }
    }

    fun truncate(length: Int) {
        require(length >= 0) { "length must be postiive: $length" }
        if (size <= length) return
        if (length == 0) clear()

        val h = head!!
        val newTail = (1 until length).fold(h) { node, _ -> node.next!! }
        tail = newTail
        newTail.next = null
        size = length
    }

    override fun clear() {
        head = null
        tail = null
        size = 0
    }
}
