import ui.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun toHex() {
        val red = Color(255, 0, 0)
        assertEquals(0xFF0000, red.toHex())
        val someColor = Color(192, 58, 201)
        assertEquals(0xC03AC9, someColor.toHex())
    }
}