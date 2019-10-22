package com.romney.fractals

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import tornadofx.*

val STABILITY_ZONE = -2.0..2.0

class FractalImageGenerator : Controller() {

    fun generateMandelbrotImage(width: Double, height: Double, iterations: Int) =
            generateImage(width, height) { x, y ->
                getMandelbrotStability(x, y, width, iterations)
            }

    fun generateJuliaImage(width: Double, height: Double, iterations: Int, juliaConstant: ComplexNumber) =
            generateImage(width, height) { x, y ->
                getJuliaStability(x, y, width, iterations, juliaConstant)
            }

    private fun generateImage(width: Double, height: Double, stabilityFunction: (x: Int, y: Int) -> Double): Image {
        val image = WritableImage(width.toInt(), height.toInt())
        // iterates through every pixel calculating and setting it's color
        (0 until width.toInt()).forEach { x ->
            (0 until height.toInt()).forEach { y ->
                val stability = stabilityFunction(x, y)
                setPixel(x, y, stability, image)
            }
        }
        return image
    }

    private fun setPixel(x: Int,
                         y: Int,
                         stability: Double,
                         image: WritableImage) {

        if (stability in .9999..1.0001) {
            // stable coordinates are dark
            image.pixelWriter.setColor(x, y, Color(0.05, 0.0, 0.1, 1.0))
        } else {
            // unstable coordinates are lighter based on stability
            val blue = (stability + 0.17).coerceIn(0.0..1.0)
            val green = blue / 1.15
            val red = green / 1.15
            image.pixelWriter.setColor(x, y, Color(red, green, blue, 1.0))
        }
    }

    private fun getJuliaStability(x: Int,
                                  y: Int,
                                  canvasWidth: Double,
                                  iterations: Int,
                                  juliaConstant: ComplexNumber) =
            getPointStability(iterations, coordToComplex(x, y, canvasWidth), juliaConstant)

    private fun getMandelbrotStability(x: Int,
                                       y: Int,
                                       canvasWidth: Double,
                                       iterations: Int) =
            getPointStability(iterations, 0.i, coordToComplex(x, y, canvasWidth))

    // convert pixel coordinates to graph coordinates
    private fun coordToComplex(x: Int, y: Int, canvasWidth: Double): ComplexNumber {
        val (graphX, graphY) = screenCoordsToZeroCenteredCartesianCoords(
                x.toDouble(), y.toDouble(), GRAPH_WIDTH, GRAPH_HEIGHT, canvasWidth)
        return graphX + graphY.i
    }

    private fun getPointStability(iterations: Int, a: ComplexNumber, constant: ComplexNumber): Double {
        // fractal set iteration. This is the meat of the fractal set calculations
        var complexNumber = a
        var iterationCount = 0
        for (i in 1..iterations) {
            complexNumber = (complexNumber * complexNumber) + constant
            if (complexNumber.real !in STABILITY_ZONE || complexNumber.imaginary !in STABILITY_ZONE) {
                // this optimization prevents unnecessary iterations. Coordinates outside of STABILITY_ZONE have exploded
                // and are unstable. This optimization greatly improves rendering performance
                break
            }
            iterationCount++
        }
        return iterationCount / iterations.toDouble()
    }
}