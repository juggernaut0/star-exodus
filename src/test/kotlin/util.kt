import kotlin.test.*
import util.*

class ExtsTest {
    @Test
    fun scan() {
        val expected = listOf(0, 1, 3, 6, 10, 15)
        val actual = (1..5).toList().scan(0, { a, b -> a + b})
        assertEquals(expected, actual)
    }
}

class WeightedListTest {
    @Test
    fun get(){
        val wl = WeightedList("a" to 2, "b" to 1, "c" to 3)
        assertEquals("a", wl[0])
        assertEquals("a", wl[1])
        assertEquals("b", wl[2])
        assertEquals("c", wl[3])
        assertEquals("c", wl[5])
        assertFailsWith<IndexOutOfBoundsException> { wl[6] }
    }

    @Test
    fun size(){
        val wl = WeightedList("a" to 2, "b" to 1, "c" to 3)
        assertEquals(6, wl.size)
    }
}
