package me.juangoncalves.mentra.features.stats.mapper

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.MainCoroutineRule
import me.juangoncalves.mentra.at
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.usecases.preference.GetTimeUnitPreference
import me.juangoncalves.mentra.toRight
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class TimeChartMapperTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    //endregion

    //region Mocks
    @MockK lateinit var getTimeUnitPreferenceMock: GetTimeUnitPreference
    //endregion

    private lateinit var sut: TimeChartMapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = TimeChartMapper(getTimeUnitPreferenceMock, mainCoroutineRule.dispatcher)
    }

    @Test
    fun `returns the appropriate labels for the received daily history`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Daily.toRight()
        val prices = listOf(
            10 at LocalDateTime.of(2020, 10, 20, 10, 23),
            15 at LocalDateTime.of(2020, 10, 21, 7, 12),
            13 at LocalDateTime.of(2020, 10, 22, 22, 23),
            17 at LocalDateTime.of(2020, 10, 23, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertEquals("10/20/20", result.labels[0])
        assertEquals("10/21/20", result.labels[1])
        assertEquals("10/22/20", result.labels[2])
        assertEquals("10/23/20", result.labels[3])
    }

    @Test
    fun `returns the appropriate entries for the received daily history`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Daily.toRight()
        val prices = listOf(
            10 at LocalDateTime.of(2020, 10, 20, 10, 23),
            15 at LocalDateTime.of(2020, 10, 21, 7, 12),
            13 at LocalDateTime.of(2020, 10, 22, 22, 23),
            17 at LocalDateTime.of(2020, 10, 23, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertThat(result.entries[0].y.toDouble(), closeTo(10.0, 0.0001))
        assertThat(result.entries[1].y.toDouble(), closeTo(15.0, 0.0001))
        assertThat(result.entries[2].y.toDouble(), closeTo(13.0, 0.0001))
        assertThat(result.entries[3].y.toDouble(), closeTo(17.0, 0.0001))
    }

    @Test
    fun `returns the time granularity value available at the time`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Daily.toRight()
        val prices = listOf(
            10 at LocalDateTime.of(2020, 10, 20, 10, 23),
            15 at LocalDateTime.of(2020, 10, 21, 7, 12),
            13 at LocalDateTime.of(2020, 10, 22, 22, 23),
            17 at LocalDateTime.of(2020, 10, 23, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertEquals(TimeGranularity.Daily, result.granularity)
    }

    @Test
    fun `returns the appropriate labels for the weekly history`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Weekly.toRight()
        val prices = listOf(
            11 at LocalDateTime.of(2020, 10, 11, 1, 15),
            21 at LocalDateTime.of(2020, 10, 21, 1, 15),
            27 at LocalDateTime.of(2020, 10, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertEquals(3, result.entries.size)
        assertThat(result.entries[0].y.toDouble(), closeTo(11.0, 0.0001))
        assertThat(result.entries[1].y.toDouble(), closeTo(21.0, 0.0001))
        assertThat(result.entries[2].y.toDouble(), closeTo(27.0, 0.0001))
    }

    @Test
    fun `returns the appropriate entries for the weekly history`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Weekly.toRight()
        val prices = listOf(
            11 at LocalDateTime.of(2020, 10, 11, 1, 15),
            21 at LocalDateTime.of(2020, 10, 21, 1, 15),
            27 at LocalDateTime.of(2020, 10, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertEquals(3, result.entries.size)
        assertEquals("Oct 2020 - W3", result.labels[0])
        assertEquals("Oct 2020 - W4", result.labels[1])
        assertEquals("Oct 2020 - W5", result.labels[2])
    }

    @Test
    fun `returns the appropriate entries for the monthly history`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Monthly.toRight()
        val prices = listOf(
            11 at LocalDateTime.of(2020, 11, 11, 1, 15),
            21 at LocalDateTime.of(2020, 12, 21, 1, 15),
            27 at LocalDateTime.of(2021, 1, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertEquals(3, result.entries.size)
        assertThat(result.entries[0].y.toDouble(), closeTo(11.0, 0.0001))
        assertThat(result.entries[1].y.toDouble(), closeTo(21.0, 0.0001))
        assertThat(result.entries[2].y.toDouble(), closeTo(27.0, 0.0001))
    }

    @Test
    fun `returns the appropriate labels for the monthly history`() = runBlockingTest {
        // Arrange
        coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Monthly.toRight()
        val prices = listOf(
            11 at LocalDateTime.of(2020, 11, 11, 1, 15),
            21 at LocalDateTime.of(2020, 12, 21, 1, 15),
            27 at LocalDateTime.of(2021, 1, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        assertEquals(3, result.entries.size)
        assertEquals("Nov 2020", result.labels[0])
        assertEquals("Dec 2020", result.labels[1])
        assertEquals("Jan 2021", result.labels[2])
    }

    @Test
    fun `returns an empty list of labels and entries when the price history is empty`() =
        runBlockingTest {
            // Arrange
            coEvery { getTimeUnitPreferenceMock.invoke(any()) } returns TimeGranularity.Monthly.toRight()

            // Act
            val result = sut.map(emptyList())

            // Assert
            assertEquals(0, result.labels.size)
            assertEquals(0, result.entries.size)
        }

    //region Helpers
    //endregion

}