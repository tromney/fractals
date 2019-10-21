package com.romney.fractals

import org.junit.Test

class ComplexNumberTest {

    @Test
    fun testAdd() {
        5 + 2.i + 3 assertIs 8 + 2.i
    }

    @Test
    fun testSubtract() {
        (6 + 2.i) - (3 - 1.i) assertIs 3 + 3.i
    }

    @Test
    fun testMultiply() {
        (2 + 2.i) * (3 + 3.i) assertIs 12.i
    }

    @Test
    fun testDivide() {
        (4 + 8.i) / (2 + 4.i) assertIs 2 + 0.i
    }
}