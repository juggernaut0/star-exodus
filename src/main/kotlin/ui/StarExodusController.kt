package ui

import angular.HttpService
import game.ExodusGame
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

@Suppress("MemberVisibilityCanPrivate")
class StarExodusController(http: HttpService) {
    private val canvas: CanvasDraw
    private lateinit var game: ExodusGame

    var clickedStarName: String? = null

    init {
        val loader = HttpResourceLoader(http)
        loader.fetchResources().then({ game = ExodusGame(loader); refreshMap() })

        val elem = document.getElementById("mapCanvas")
        if (elem == null || elem !is HTMLCanvasElement) throw IllegalStateException("Cannot find map canvas")

        canvas = CanvasDraw(elem.getContext("2d") as CanvasRenderingContext2D)
    }

    @JsName("refreshMap")
    fun refreshMap() {
        canvas.clear(Color.BLACK)
        for (star in game.galaxy.stars) {
            val x = star.location.x * canvas.width / game.galaxy.mapSize
            val y = star.location.y * canvas.height / game.galaxy.mapSize
            canvas.circle(Point(x, y), 1.0, lineStyle = LineStyle(Color.TRANSPARENT), fillColor = Color.WHITE) {
                clickedStarName = star.name
            }
        }
    }
}
