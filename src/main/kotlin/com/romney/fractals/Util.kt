package com.romney.fractals

import javafx.scene.control.TextField

// eg (-2.0, 1.5, 1024.0, 768.0, 4.0)
fun zeroCenteredCartesianCoordsToScreenCoords(x: Double,
                                              y: Double,
                                              canvasWidth: Double,
                                              canvasHeight: Double,
                                              graphWidth: Double): Pair<Double, Double> {

    val ratio = canvasHeight / canvasWidth
    val currentHeight = graphWidth * ratio

    val scale = canvasWidth / graphWidth

    val shiftedX = x + (graphWidth / 2)
    val shiftedY = currentHeight - (y + currentHeight / 2)

    val newX = scale * shiftedX
    val newY = scale * shiftedY

    return newX to newY
}

fun getPointsFromJuliaSetIteration(screenX: Double,
                                   screenY: Double,
                                   iterations: Int,
                                   juliaConstant: ComplexNumber): List<Pair<Double, Double>> {

    val (x, y) = screenCoordsToZeroCenteredCartesianCoords(
            screenX, screenY, GRAPH_WIDTH, GRAPH_HEIGHT, CANVAS_WIDTH)

    return getPointsFromFractalIteration(iterations, x + y.i, juliaConstant)
}

fun TextField.toIntOrZero() = this.text.toIntOrNull() ?: 0
fun TextField.toDoubleOrZero() = this.text.toDoubleOrNull() ?: 0.0

fun getPointsFromMandlebrotIteration(screenX: Double,
                                            screenY: Double,
                                            iterations: Int): List<Pair<Double, Double>> {

    val (x, y) = screenCoordsToZeroCenteredCartesianCoords(
            screenX, screenY, GRAPH_WIDTH, GRAPH_HEIGHT, CANVAS_WIDTH)

    return getPointsFromFractalIteration(iterations, 0.i, x + y.i)
}

private fun getPointsFromFractalIteration(iterations: Int,
                                          complexNumber: ComplexNumber,
                                          constant: ComplexNumber): List<Pair<Double, Double>> {
    var complexNumber1 = complexNumber
    return (1..iterations).map {
        val point = zeroCenteredCartesianCoordsToScreenCoords(
                complexNumber1.real, complexNumber1.imaginary, CANVAS_WIDTH, CANVAS_HEIGHT, GRAPH_WIDTH)
        complexNumber1 = (complexNumber1 * complexNumber1) + constant
        point
    }
}

// eg (1024.0, 768.0, 4.0, 3.0, 1024.0) -> 2.0, -1.5
fun screenCoordsToZeroCenteredCartesianCoords(screenX: Double,
                                              screenY: Double,
                                              translatedGraphWidth: Double,
                                              translatedGraphHeight: Double,
                                              canvasWidth: Double): Pair<Double, Double> {

    val scale = translatedGraphWidth / canvasWidth
    val scaledX = screenX * scale
    val scaledY = screenY * scale

    val shiftedX = scaledX - translatedGraphWidth / 2
    val shiftedY = translatedGraphHeight / 2 - scaledY

    return shiftedX to shiftedY
}