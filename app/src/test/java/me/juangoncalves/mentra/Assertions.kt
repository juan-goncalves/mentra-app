package me.juangoncalves.mentra

import junit.framework.TestCase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers

inline infix fun <reified T> T?.equals(other: T?) = TestCase.assertEquals(other, this)

infix fun Double.closeTo(value: Double) =
    assertThat(this, Matchers.closeTo(value, 0.0001))
