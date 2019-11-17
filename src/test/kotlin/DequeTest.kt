import util.Deque
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DequeTest {
    @Test
    fun pushPop() {
        val d = Deque<Int>()
        assertTrue { d.isEmpty() }
        d.pushFront(1)
        assertTrue { d.isNotEmpty() }
        assertEquals(listOf(1), d.toList())
        d.pushBack(2)
        assertEquals(listOf(1, 2), d.toList())
        d.pushFront(3)
        assertEquals(listOf(3, 1, 2), d.toList())
        val two = d.popBack()
        assertEquals(listOf(3, 1), d.toList())
        assertEquals(2, two)
        val three = d.popFront()
        assertEquals(listOf(1), d.toList())
        assertEquals(3, three)
    }

    @Test
    fun iterator() {
        val d = Deque(listOf(1, 2, 3))
        val it = d.iterator()
        assertTrue { it.hasNext() }
        assertEquals(1, it.next())
        assertTrue { it.hasNext() }
        assertEquals(2, it.next())
        assertTrue { it.hasNext() }
        assertEquals(3, it.next())
        assertFalse { it.hasNext() }
    }

    @Test
    fun mutableIterator() {
        val d = Deque(listOf(1, 2, 3))
        val it = d.iterator()
        it.next()
        it.next()
        it.remove()
        assertEquals(listOf(1, 3), d.toList())
    }
}