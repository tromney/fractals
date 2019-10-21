package com.romney.fractals

import org.hamcrest.CoreMatchers
import org.junit.Assert

infix fun Any.assertIs(any: Any) {
    Assert.assertThat(this, CoreMatchers.`is`(any))
}