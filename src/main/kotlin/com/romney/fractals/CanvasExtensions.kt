package com.romney.fractals

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

fun Canvas.drawGrid(minX: Double, maxX: Double, minY: Double, maxY: Double) {

    val xMid = this.width / 2
    val yMid = this.height / 2

    val gc = this.graphicsContext2D
    val textColor = Color(.8, .8, 1.0, 1.0)
    val lineColor = Color(.6, .6, .9, .5)

    gc.fill = lineColor
    gc.fillRect(xMid - 2, 0.0, 2.0, this.height)
    gc.fillRect(0.0, yMid - 2, this.width, 2.0)

    val increments = 20

    val xIncrements = width / increments
    val yIncrements = height / increments
    var currentX = 0.0
    var currentY = 0.0

    repeat(increments) {
        gc.fill = lineColor
        gc.fillRect(currentX - 3, yMid - 10, 2.0, 20.0)
        gc.fillRect(xMid - 10, currentY - 3, 20.0, 2.0)

        val (x, _) =
                screenCoordsToZeroCenteredCartesianCoords(currentX, yMid, maxX - minX, maxY, width)
        val (_, y) =
                screenCoordsToZeroCenteredCartesianCoords(xMid, currentY, maxX - minX, maxY - minY, width)

        gc.fill = textColor
        if (y !in -0.001..0.001) {
            gc.fillText("%.2f".format(y), xMid + 15, currentY + 3)
        }
        if (x in -0.001..0.001) {
            gc.fill = Color(.05, .05, .05, .8)
            gc.fillRect(currentX - 26, currentY + 10, 50.0, 20.0)
            gc.fill = textColor
            gc.fillText("%.2f".format(.0), currentX - 15, yMid + 25)
        } else {
            gc.fill = textColor
            gc.fillText("%.2f".format(x), currentX - 15, yMid + 25)
        }
        currentX += xIncrements
        currentY += yIncrements
    }
}

fun Canvas.drawConnectedPoints(points: List<Pair<Double, Double>>,
                               drawConnectingLines: Boolean = true,
                               color: Color = Color.ALICEBLUE,
                               radius: Double = 7.0) {

    val gc = this.graphicsContext2D
    val darkStroke = color.darker().darker().darker()

    var prevPoint: Pair<Double, Double>? = null
    points.forEach { (x, y) ->
        gc.fill = color
        gc.fillOval(x - radius / 2, y - radius / 2, radius, radius)
        gc.lineWidth = 1.0
        gc.stroke = darkStroke
        gc.strokeOval(x - radius / 2, y - radius / 2, radius, radius)
        if (drawConnectingLines) {
            if (prevPoint != null) {
                gc.stroke = color.darker()
                gc.lineWidth = 2.0
                gc.strokeLine(prevPoint!!.first, prevPoint!!.second, x, y)
            }
            prevPoint = x to y
        }
    }
}