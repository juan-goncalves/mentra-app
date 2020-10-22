package me.juangoncalves.pie.unlabeled

import me.juangoncalves.pie.PiePortion
import me.juangoncalves.pie.PiePortionValidator
import org.junit.Before
import org.junit.Test


class PiePortionValidatorTest {

    private lateinit var sut: PiePortionValidator

    @Before
    fun setUp() {
        sut = PiePortionValidator()
    }

    @Test
    fun `validatePortions doesn't throw an exception if the portions sum is close to 1`() {
        // Arrange
        val portions = arrayOf(
            PiePortion(0.45, "Coin 1"),
            PiePortion(0.25, "Coin 2"),
            PiePortion(0.15, "Coin 3"),
            PiePortion(0.15, "Coin 4")
        )

        // Act
        sut.validatePortions(portions)
    }

    @Test
    fun `validatePortions doesn't throw an exception if the portion list is empty`() {
        // Arrange
        val portions = emptyArray<PiePortion>()

        // Act
        sut.validatePortions(portions)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `validatePortions throws a IllegalArgumentException if the portions sum is not close to 1`() {
        // Arrange
        val portions = arrayOf(
            PiePortion(0.45, "Coin 1"),
            PiePortion(0.10, "Coin 2"),
            PiePortion(0.10, "Coin 3")
        )

        // Act
        sut.validatePortions(portions)
    }

}