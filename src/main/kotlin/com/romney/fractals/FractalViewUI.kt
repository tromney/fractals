package com.romney.fractals

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.Cursor
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

const val CANVAS_WIDTH = 1024.0
const val CANVAS_HEIGHT = 768.0
const val GRAPH_WIDTH = 4.0
const val GRAPH_HEIGHT = 3.0
const val ITERATION_ANIMATION_MAX = 200

class FractalViewUI : View() {
    override fun onUndock() {
        scheduler.shutdown()
    }

    private var canvas: Canvas by singleAssign()
    private var juliaOverlay: Canvas by singleAssign()
    private var iterationsField: TextField by singleAssign()
    private var realField: TextField by singleAssign()
    private var imaginaryField: TextField by singleAssign()
    private var presetsCombo: ComboBox<PreSet> by singleAssign()
    private var drawGridCb: CheckBox by singleAssign()
    private var drawImageCb: CheckBox by singleAssign()
    private var movingCircleLabel: Label by singleAssign()
    private var mandelbrotRadio: RadioButton by singleAssign()
    private var juliaSetRadio: RadioButton by singleAssign()
    private var drawConnectingLinesCb: CheckBox by singleAssign()
    private var juliaOverlayCb: CheckBox by singleAssign()
    private var showMandelbrotBtn: Button by singleAssign()
    private val movingCircle = MovingCircle(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 6.0, Color.RED)

    private val imageController: FractalImageGenerator by inject()

    private var scheduler = Executors.newScheduledThreadPool(1)
    private var animatingImaginary = false
    private var animatingReal = false
    private var animatingIterations = false
    private var cachedImage: Image? = null

    override val root =
            vbox {
                stackpane {
                    canvas = canvas(CANVAS_WIDTH, CANVAS_HEIGHT)
                    this.add(movingCircle)
                    movingCircle.setOnCircleMoved { x, y, graphX, graphY ->
                        drawCachedImage()
                        movingCircleLabel.text = "($graphX, $graphY)"
                        if (mandelbrotRadio.isSelected) {
                            realField.text = "$graphX"
                            imaginaryField.text = "$graphY"
                        }
                        if (juliaOverlayCb.isSelected) {
                            drawJuliaOverlay()
                        } else {
                            canvas.drawConnectedPoints(getFractalPoints(x, y), drawConnectingLinesCb.isSelected)
                        }
                    }
                    juliaOverlay = canvas(341.0, 256.0) {
                        translateX = CANVAS_WIDTH / 2 - width / 2
                        translateY = -(CANVAS_HEIGHT / 2 -  height / 2)
                        cursor = Cursor.HAND
                        setOnMouseClicked {
                            showMandelbrotBtn.isDisable = false
                            val (x, y) = movingCircleLabel.text.removeSurrounding("(", ")").split(",")
                            disableJuliaOverlay()
                            juliaSetRadio.isSelected = true
                            realField.text = x.trim()
                            imaginaryField.text = y.trim()
                            drawImageCb.isSelected = true
                            renderImage()
                        }
                    }
                }
                hbox {
                    form {
                        fieldset {
                            field {
                                label("Location:")
                                movingCircleLabel = label("(0.0, 0.0)")
                            }
                        }
                        fieldset("Settings") {
                            field {
                                drawGridCb = checkbox {
                                    isSelected = true
                                    setOnAction {
                                        renderImage()
                                        if (isSelected) {
                                            drawGrid()
                                        }
                                    }
                                }
                                label("Show Grid    ")
                                drawImageCb = checkbox {
                                    setOnAction {
                                        renderImage()
                                    }
                                }
                                label("Show Image    ")
                                drawConnectingLinesCb = checkbox {
                                    setOnAction {
                                        drawCachedImage()
                                        canvas.drawConnectedPoints(
                                                getFractalPoints(movingCircle.getX(),  movingCircle.getY()),
                                                drawConnectingLinesCb.isSelected)
                                    }
                                }
                                label("Show Connecting Lines    ")
                                togglegroup {
                                    juliaSetRadio = radiobutton {
                                        isSelected = true
                                        setOnAction {
                                            showMandelbrotBtn.isDisable = false
                                            renderImage()
                                            disableJuliaOverlay()
                                        }
                                    }
                                    label("Julia Set    ")
                                    mandelbrotRadio = radiobutton {
                                        setOnAction {
                                            showMandelbrotBtn.isDisable = true
                                            if (this.isSelected) {
                                                iterationsField.text = "45"
                                            }
                                            renderImage()
                                            juliaOverlayCb.isDisable = false
                                        }
                                    }
                                    label("Mandelbrot    ")
                                }

                                checkbox {
                                    isSelected = movingCircle.getFixToXAxis()
                                    setOnAction {
                                        movingCircle.setFixToXAxis(this.isSelected)
                                    }
                                }
                                label("Fix To X-Axis    ")
                                juliaOverlayCb = checkbox {
                                    isDisable = true
                                    setOnAction {
                                        juliaOverlay.isVisible = this.isSelected
                                        if (this.isSelected) {
                                            juliaOverlay.show()
                                        }
                                        renderImage()
                                    }
                                }
                                label("Julia Overlay    ")
                            }
                            field("Iterations:") {
                                iterationsField = textfield {
                                    setOnAction { renderImage() }
                                }
                                button("Animate") {
                                    setOnAction {
                                        animatingIterations = !animatingIterations
                                        text = animationButtonText(animatingIterations)
                                        animateIterations()
                                    }
                                }
                            }
                        }
                        fieldset("Constant") {
                            field("Real Part:") {
                                realField = textfield {
                                    setOnAction { renderImage() }
                                }
                                button("Animate") {
                                    setOnAction {
                                        animatingReal = !animatingReal
                                        text = animationButtonText(animatingReal)
                                        animate()
                                    }
                                }
                            }
                            field("Imaginary Part:") {
                                imaginaryField = textfield {
                                    setOnAction { renderImage() }
                                }
                                button("Animate") {
                                    setOnAction {
                                        animatingImaginary = !animatingImaginary
                                        text = animationButtonText(animatingImaginary)
                                        animate()
                                    }
                                }
                                showMandelbrotBtn = button("Show In Mandelbrot") {
                                    setOnAction {
                                        mandelbrotRadio.isSelected = true
                                        val complex = realField.toDoubleOrZero() + imaginaryField.toDoubleOrZero().i
                                        movingCircle.moveTo(complex)
                                        drawImageCb.isSelected = true
                                        juliaOverlayCb.isSelected = true
                                        juliaOverlayCb.isDisable = false
                                        juliaOverlay.show()
                                        movingCircleLabel.text = "(${realField.toDoubleOrZero()}, ${imaginaryField.toDoubleOrZero()})"
                                        renderImage()
                                        drawJuliaOverlay()
                                        this.isDisable = true
                                    }
                                }
                            }
                            field("Pre-Sets:") {
                                presetsCombo = combobox(values = JuliaSetPreSets.PRESETS) {
                                    setOnAction {
                                        iterationsField.text = this.selectedItem?.iterations?.toString() ?: "0"
                                        realField.text = this.selectedItem?.constant?.real?.toString()
                                                ?: "0.0"
                                        imaginaryField.text = this.selectedItem?.constant?.imaginary?.toString()
                                                ?: "0.0"
                                        juliaSetRadio.isSelected = true
                                        showMandelbrotBtn.isDisable = false
                                        renderImage()
                                        disableJuliaOverlay()
                                    }
                                }
                                button("Reset") {
                                    setOnAction {
                                        presetsCombo.fireEvent(ActionEvent())
                                        movingCircle.move(
                                                Duration.millis(200.0),
                                                javafx.geometry.Point2D(0.0, 0.0))
                                    }
                                }
                            }
                        }
                    }
                }
                presetsCombo.selectionModel.selectFirst()
                presetsCombo.fireEvent(ActionEvent())
            }

    private fun drawJuliaOverlay() {
        val image = imageController.generateJuliaImage(
                juliaOverlay.width,
                juliaOverlay.height,
                iterationsField.toIntOrZero(),
                movingCircle.complexCoords(CANVAS_WIDTH))

        juliaOverlay.graphicsContext2D.drawImage(image, 0.0, 0.0)
    }

    private fun disableJuliaOverlay() {
        juliaOverlayCb.isSelected = false
        juliaOverlayCb.isDisable = true
        juliaOverlay.hide()
    }

    private fun getFractalPoints(x: Double, y: Double): List<Pair<Double, Double>> {
        return if (juliaSetRadio.isSelected) {
            getPointsFromJuliaSetIteration(
                    x,
                    y,
                    iterationsField.toIntOrZero(),
                    (realField.toDoubleOrZero()) + (imaginaryField.toDoubleOrZero()).i)
        } else {
            getPointsFromMandlebrotIteration(x, y, iterationsField.toIntOrZero())
        }
    }

    private fun drawCachedImage() {
        if (cachedImage == null) {
            renderImage()
        } else {
            if (drawImageCb.isSelected) {
                canvas.graphicsContext2D.drawImage(cachedImage, 0.0, 0.0, CANVAS_WIDTH, CANVAS_HEIGHT)
            } else {
                clearCanvas()
            }
            if (drawGridCb.isSelected) {
                drawGrid()
            }
        }
    }

    private fun animationButtonText(animating: Boolean) = if (animating) "Stop" else "Animate"

    private fun TextField.increment() {
        var curVal = text.toDoubleOrNull() ?: 0.0
        curVal += 0.001
        Platform.runLater {
            text = curVal.toString()
        }
    }

    private fun animateIterations() {
        animate(animatingIterations, this::animateIterations) {
            iterationsField.text = ((iterationsField.text.toInt() + 1) % ITERATION_ANIMATION_MAX).toString()
        }
    }

    private fun animate(animate: Boolean, schedulerFunction: () -> Unit, incrementFunction: () -> Unit) {
        if (animate) {
            incrementFunction()
            if (mandelbrotRadio.isSelected) {
                movingCircle.moveTo(realField.toDoubleOrZero() + imaginaryField.toDoubleOrZero().i)
                juliaOverlayCb.isSelected = true
                juliaOverlay.show()
                drawJuliaOverlay()
            } else {
                movingCircle.hide()
                renderImage()
            }

            scheduler.schedule({
                schedulerFunction()
            }, 20, TimeUnit.MILLISECONDS)
        } else {
            movingCircle.show()
        }
    }

    private fun animate() {
        animate(animatingImaginary || animatingReal, this::animate) {
            if (animatingImaginary) {
                imaginaryField.increment()
            }
            if (animatingReal) {
                realField.increment()
            }
        }
    }

    private fun renderImage() {
        if (drawImageCb.isSelected) {

            val iterations = iterationsField.toIntOrZero()
            val imaginary = imaginaryField.toDoubleOrZero()
            val real = realField.toDoubleOrZero()

            val gc = canvas.graphicsContext2D
            cachedImage = if (juliaSetRadio.isSelected) {
                imageController.generateJuliaImage(CANVAS_WIDTH, CANVAS_HEIGHT,
                        iterations, real + imaginary.i)
            } else {
                imageController.generateMandelbrotImage(CANVAS_WIDTH, CANVAS_HEIGHT, iterations)
            }

            gc.drawImage(cachedImage, 0.0, 0.0, CANVAS_WIDTH, CANVAS_HEIGHT)
        } else {
            clearCanvas()
        }

        if (drawGridCb.isSelected) {
            drawGrid()
        }
    }

    private fun clearCanvas() {
        canvas.graphicsContext2D.fill = Color(.1, .1, .1, 1.0)
        canvas.graphicsContext2D.fillRect(0.0, 0.0, CANVAS_WIDTH, CANVAS_HEIGHT)
    }

    private fun drawGrid() {
        canvas.drawGrid(-2.0, 2.0, -1.5, 1.5)
    }
}