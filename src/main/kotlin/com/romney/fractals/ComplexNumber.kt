package com.romney.fractals

data class ComplexNumber(val real: Double, val imaginary: Double) {

    infix operator fun plus(number: Double) =
            ComplexNumber(real + number, imaginary)

    infix operator fun plus(number: Int) = this + number.toDouble()

    infix operator fun minus(number: Double) =
            ComplexNumber(real - number, imaginary)

    infix operator fun minus(number: Int) = this - number.toDouble()

    infix operator fun times(number: Double) =
            ComplexNumber(this.real * number, this.imaginary * number)

    infix operator fun times(number: Int) = this * number.toDouble()

    infix operator fun div(number: Double) =
            ComplexNumber(this.real / number, this.imaginary / number)

    infix operator fun div(number: Int) = this / number.toDouble()

    infix operator fun plus(complexNumber: ComplexNumber) =
            ComplexNumber(this.real + complexNumber.real, this.imaginary + complexNumber.imaginary)

    infix operator fun minus(complexNumber: ComplexNumber) =
            ComplexNumber(this.real - complexNumber.real, this.imaginary - complexNumber.imaginary)

    infix operator fun times(complexNumber: ComplexNumber): ComplexNumber {
        val first = this.real * complexNumber.real
        val outside = this.real * complexNumber.imaginary
        val inside = this.imaginary * complexNumber.real
        val last = -1 * (this.imaginary * complexNumber.imaginary)
        return ComplexNumber(first + last, outside + inside)
    }

    infix operator fun div(complexNumber: ComplexNumber): ComplexNumber {
        val complexConjugate = ComplexNumber(complexNumber.real, -complexNumber.imaginary)
        val numerator = this * complexConjugate
        val denominator = complexNumber * complexConjugate
        return ComplexNumber(numerator.real / denominator.real, numerator.imaginary / denominator.real)
    }

    operator fun unaryMinus() = ComplexNumber(-this.real, -this.imaginary)
}

val Double.i: ComplexNumber
    get() = ComplexNumber(0.0, this)

val Int.i: ComplexNumber
    get() = ComplexNumber(0.0, this.toDouble())

infix operator fun Double.plus(complexNumber: ComplexNumber) =
        ComplexNumber(this, complexNumber.imaginary)

infix operator fun Int.plus(complexNumber: ComplexNumber) = this.toDouble() + complexNumber

infix operator fun Double.minus(complexNumber: ComplexNumber) =
        ComplexNumber(this, -complexNumber.imaginary)

infix operator fun Int.minus(complexNumber: ComplexNumber) = this.toDouble() - complexNumber

infix operator fun Double.times(complexNumber: ComplexNumber) =
        ComplexNumber(this * complexNumber.real, this * complexNumber.imaginary)

infix operator fun Int.times(complexNumber: ComplexNumber) = this.toDouble() * complexNumber

infix operator fun Double.div(complexNumber: ComplexNumber) =
        ComplexNumber(this / complexNumber.real, this / complexNumber.imaginary)

infix operator fun Int.div(complexNumber: ComplexNumber) = this.toDouble() / complexNumber