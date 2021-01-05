package me.juangoncalves.pie.domain

import me.juangoncalves.pie.PiePortion
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test


class PieManagerTest {

    private lateinit var sut: PieManager

    @Before
    fun setUp() {
        sut = PieManager()
    }

    @Test
    fun `reducePortions returns the received portions without change if they are no small ones`() {
        // Arrange
        val portions = arrayOf(
            PiePortion(0.80, "Coin 1"),
            PiePortion(0.10, "Coin 2"),
            PiePortion(0.10, "Coin 3")
        )

        // Act
        val result = sut.reducePortions(portions, "Merged")

        // Assert
        assertThat(result, `is`(portions))
    }

    @Test
    fun `reducePortions merges all of the small portions into a single one`() {
        // Arrange
        val portions = arrayOf(
            PiePortion(0.80, "Portion 1"),
            PiePortion(0.15, "Portion 2"),
            PiePortion(0.01, "Portion 3"),
            PiePortion(0.02, "Portion 4"),
            PiePortion(0.02, "Portion 5")
        )

        // Act
        val result = sut.reducePortions(portions, "Other portions")

        // Assert
        val expected = arrayOf(
            PiePortion(0.80, "Portion 1"),
            PiePortion(0.15, "Portion 2"),
            PiePortion(0.05, "Other portions")
        )
        assertArrayEquals(result, expected)
    }

    @Test
    fun `reducePortions returns the received portions without change if there's only one small portion`() {
        // Arrange
        val portions = arrayOf(
            PiePortion(0.80, "Portion 1"),
            PiePortion(0.1, "Portion 2"),
            PiePortion(0.09, "Portion 3"),
            PiePortion(0.01, "Small")
        )

        // Act
        val result = sut.reducePortions(portions, "Other portions")

        // Assert
        val expected = arrayOf(
            PiePortion(0.80, "Portion 1"),
            PiePortion(0.1, "Portion 2"),
            PiePortion(0.09, "Portion 3"),
            PiePortion(0.01, "Small")
        )
        assertArrayEquals(result, expected)
    }
}