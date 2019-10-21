package com.romney.fractals

import tornadofx.*

class FractalViewerApp : App(FractalViewUI::class) {
    init {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
        }
    }
}