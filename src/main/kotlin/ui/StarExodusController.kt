package ui

import angular.HttpService
import game.ExodusGame
import game.ResourceLoader
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

@Suppress("MemberVisibilityCanPrivate")
class StarExodusController(http: HttpService) {
    private val canvas: CanvasDraw
    private val game: ExodusGame = ExodusGame(object : ResourceLoader {
        override fun getStarNames(): List<String> {
            http.get<String>("/star_names.txt").then({ it.data.split('\n') })
        }

        override fun getShipNames(): List<String> {
            TODO("not implemented")
        }
    })

    init {
        val elem = document.getElementById("mapCanvas")
        if (elem == null || elem !is HTMLCanvasElement) throw IllegalStateException("Cannot find map canvas")

        canvas = CanvasDraw(elem.getContext("2d") as CanvasRenderingContext2D)
        refreshMap()
    }

    @JsName("refreshMap")
    fun refreshMap() {
        canvas.clear(Color.BLACK)
        for (star in game.galaxy.stars) {

        }
        canvas.circle(Point(100, 100), 40.0, fillColor = Color.RED)
    }
}
