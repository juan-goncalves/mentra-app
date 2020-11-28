package me.juangoncalves.mentra

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.Is.`is`
import java.math.BigDecimal

infix fun Double.shouldBeCloseTo(value: Double) =
    assertThat(this, Matchers.closeTo(value, 0.0001))

infix fun BigDecimal.shouldBeCloseTo(value: BigDecimal) =
    assertThat(this, Matchers.closeTo(value, 0.0001.toBigDecimal()))

infix fun BigDecimal.shouldBeCloseTo(value: Double) =
    assertThat(this, Matchers.closeTo(value.toBigDecimal(), 0.0001.toBigDecimal()))

inline infix fun <reified T> T?.shouldBe(other: T?) = assertThat(this, `is`(other))
