package ui

import angular.HttpService
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

class StarExodusController(http: HttpService) {
    var text = "Foobar"
    private val canvas: HTMLCanvasElement

    init {
        val elem = document.getElementById("mapCanvas")

        if (elem == null || elem !is HTMLCanvasElement) throw IllegalStateException("Cannot find map canvas")

        canvas = elem
    }

    fun refreshMap() {
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        ctx.fillStyle = "#FF0000"
    }
}
