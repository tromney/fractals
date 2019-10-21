package com.romney.fractals

data class PreSet(
        val iterations: Int,
        val constant: ComplexNumber) {

    override fun toString() =
            "Iterations: $iterations, Constant: (${constant.real} + ${constant.imaginary}i)"
}