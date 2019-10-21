package com.romney.fractals

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

class MovingCircle(centerX: Double, centerY: Double, radius: Double, color: Color)
    : Circle(centerX, centerY, radius) {

    private var sceneX = 0.0
    private var sceneY = 0.0
    private var transX = 0.0
    private var transY = 0.0
    private var fixToXAxis = true

    private var circleMovedFunc = { _: Double, _: Double, _: Double, _: Double -> }

    init {
        fill = color

        this.setOnMousePressed { e ->
            sceneX = e.sceneX
            sceneY = e.sceneY
            transX = (e.source as Circle).translateX
            transY = (e.source as Circle).translateY
        }

        this.setOnMouseDragged { e ->

            Platform.runLater {
                translateX = (transX + e.sceneX - sceneX)
                        .coerceIn((-CANVAS_WIDTH / 2 + radius + 1)..(CANVAS_WIDTH / 2 - (radius + 1)))

                translateY = if (fixToXAxis) {
                    0.0
                } else {
                    (transY + e.sceneY - sceneY)
                            .coerceIn((-CANVAS_HEIGHT / 2 + radius + 1)..(CANVAS_HEIGHT / 2 - (radius + 1)))
                }

                val (x, y) = screenCoordsToZeroCenteredCartesianCoords(
                        e.sceneX, e.sceneY, GRAPH_WIDTH, GRAPH_HEIGHT, CANVAS_WIDTH)

                circleMovedFunc(e.sceneX, if (fixToXAxis) CANVAS_HEIGHT / 2 else e.sceneY, x, if (fixToXAxis) 0.0 else y)
            }
        }
    }

    fun getX() = CANVAS_WIDTH / 2 + translateX
    fun getY() = CANVAS_HEIGHT / 2 + translateY

    private fun getGraphX(canvasWidth: Double) =
            screenCoordsToZeroCenteredCartesianCoords(getX(), getY(), GRAPH_WIDTH, GRAPH_HEIGHT, canvasWidth).first

    private fun getGraphY(canvasWidth: Double) =
            screenCoordsToZeroCenteredCartesianCoords(getX(), getY(), GRAPH_WIDTH, GRAPH_HEIGHT, canvasWidth).second

    fun complexCoords(canvasWidth: Double) =
            getGraphX(canvasWidth) + getGraphY(canvasWidth).i

    fun moveTo(complexNumber: ComplexNumber) {
        val (x, y) = zeroCenteredCartesianCoordsToScreenCoords(
                complexNumber.real, complexNumber.imaginary, CANVAS_WIDTH, CANVAS_HEIGHT, GRAPH_WIDTH)

        translateX = x - CANVAS_WIDTH / 2
        translateY = y - CANVAS_HEIGHT / 2
    }

    fun setOnCircleMoved(func: (Double, Double, Double, Double) -> Unit) {
        circleMovedFunc = func
    }

    fun setFixToXAxis(fixToXAxis: Boolean) {
        this.fixToXAxis = fixToXAxis
        translateY = 0.0
    }

    fun getFixToXAxis() = fixToXAxis
}