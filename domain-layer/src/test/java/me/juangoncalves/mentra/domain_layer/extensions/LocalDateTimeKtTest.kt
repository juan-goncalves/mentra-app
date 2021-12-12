package me.juangoncalves.mentra.domain_layer.extensions

import io.mockk.every
import io.mockk.mockkObject
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toInstant
import me.juangoncalves.mentra.test_utils.TimeZoneTestRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LocalDateTimeKtTest {

    @get:Rule
    val timezoneRule = TimeZoneTestRule("UTC")

    @Test
    fun `minutesBetween calculates the difference correctly when the other date is after the current one`() {
        // Arrange
        val a = LocalDateTime(2021, 12, 12, 8, 30)
        val b = LocalDateTime(2021, 12, 12, 9, 45)

        // Act
        val result = a.minutesBetween(b)

        // Assert
        assertEquals(75, result)
    }

    @Test
    fun `minutesBetween calculates the difference correctly when the other date is before the current one`() {
        // Arrange
        val a = LocalDateTime(2021, 12, 12, 9, 45)
        val b = LocalDateTime(2021, 12, 12, 8, 30)

        // Act
        val result = a.minutesBetween(b)

        // Assert
        assertEquals(75, result)
    }

    @Test
    fun `daysBetween calculates the difference correctly when the other date is after the current one`() {
        // Arrange
        val a = LocalDateTime(2021, 12, 12, 9, 30)
        val b = LocalDateTime(2021, 11, 12, 9, 45)

        // Act
        val result = a.daysBetween(b)

        // Assert
        assertEquals(29, result)
    }

    @Test
    fun `daysBetween calculates the minute difference correctly when the other date is before the current one`() {
        // Arrange
        val a = LocalDateTime(2021, 11, 12, 9, 45)
        val b = LocalDateTime(2021, 12, 12, 9, 30)

        // Act
        val result = a.daysBetween(b)

        // Assert
        assertEquals(29, result)
    }

    @Test
    fun `elapsedMinutes returns the correct amount of minutes since the received date time`() =
        mockkObject(Clock.System) {
            // Arrange
            val systemTime = LocalDateTime(2021, 12, 12, 12, 12).toInstant(currentSystemDefault())
            every { Clock.System.now() } returns systemTime

            // Act
            val result = LocalDateTime(2021, 12, 12, 12, 0).elapsedMinutes()

            // Assert
            assertEquals(12, result)
        }

    @Test
    fun `elapsedDays returns the correct amount of days since the received date time`() =
        mockkObject(Clock.System) {
            // Arrange
            val systemTime = LocalDateTime(2021, 12, 12, 12, 12).toInstant(currentSystemDefault())
            every { Clock.System.now() } returns systemTime

            // Act
            val result = LocalDateTime(2021, 12, 5, 12, 0).elapsedDays()

            // Assert
            assertEquals(7, result)
        }

    @Test
    fun `now returns the correct LocalDateTime for the system's current clock time`() =
        mockkObject(Clock.System) {
            // Arrange
            val systemTime = LocalDateTime(2021, 12, 12, 12, 12)
            every { Clock.System.now() } returns systemTime.toInstant(currentSystemDefault())

            // Act
            val result = LocalDateTime.now()

            // Assert
            assertEquals(systemTime, result)
        }
}