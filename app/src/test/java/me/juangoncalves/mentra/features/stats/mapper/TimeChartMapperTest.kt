package me.juangoncalves.mentra.features.stats.mapper

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.USD
import me.juangoncalves.mentra.at
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetCurrencyPreference
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetTimeUnitPreference
import me.juangoncalves.mentra.platform.DateTimePatternProvider
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import me.juangoncalves.mentra.toRight
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TimeChartMapperTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK
    lateinit var getTimeUnitPreferenceMock: GetTimeUnitPreference

    @MockK
    lateinit var getCurrencyPreferenceMock: GetCurrencyPreference

    @MockK
    lateinit var dateTimePatternProvider: DateTimePatternProvider
    //endregion

    private lateinit var sut: TimeChartMapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        configureDefaultMocks()

        sut = TimeChartMapper(
            getTimeUnitPreferenceMock,
            getCurrencyPreferenceMock,
            dateTimePatternProvider,
        )
    }

    @Test
    fun `returns the appropriate labels for the received daily history`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Daily.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            10 at LocalDateTime(2020, 10, 20, 10, 23),
            15 at LocalDateTime(2020, 10, 21, 7, 12),
            13 at LocalDateTime(2020, 10, 22, 22, 23),
            17 at LocalDateTime(2020, 10, 23, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        with(result) {
            labels[0] shouldBe "10/20/20"
            labels[1] shouldBe "10/21/20"
            labels[2] shouldBe "10/22/20"
            labels[3] shouldBe "10/23/20"
        }
    }

    @Test
    fun `returns the appropriate entries for the received daily history`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Daily.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            10 at LocalDateTime(2020, 10, 20, 10, 23),
            15 at LocalDateTime(2020, 10, 21, 7, 12),
            13 at LocalDateTime(2020, 10, 22, 22, 23),
            17 at LocalDateTime(2020, 10, 23, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        with(result) {
            entries[0].y.toDouble() shouldBeCloseTo 10.0
            entries[1].y.toDouble() shouldBeCloseTo 15.0
            entries[2].y.toDouble() shouldBeCloseTo 13.0
            entries[3].y.toDouble() shouldBeCloseTo 17.0
        }
    }

    @Test
    fun `returns the time granularity value available at the time`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Daily.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            10 at LocalDateTime(2020, 10, 20, 10, 23),
            15 at LocalDateTime(2020, 10, 21, 7, 12),
            13 at LocalDateTime(2020, 10, 22, 22, 23),
            17 at LocalDateTime(2020, 10, 23, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        result.granularity shouldBe TimeGranularity.Daily
    }

    @Test
    fun `returns the appropriate labels for the weekly history`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Weekly.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            11 at LocalDateTime(2020, 10, 11, 1, 15),
            21 at LocalDateTime(2020, 10, 21, 1, 15),
            27 at LocalDateTime(2020, 10, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        with(result) {
            entries.size shouldBe 3
            entries[0].y.toDouble() shouldBeCloseTo 11.0
            entries[1].y.toDouble() shouldBeCloseTo 21.0
            entries[2].y.toDouble() shouldBeCloseTo 27.0
        }
    }

    @Test
    fun `returns the appropriate entries for the weekly history`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Weekly.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            11 at LocalDateTime(2020, 10, 11, 1, 15),
            21 at LocalDateTime(2020, 10, 21, 1, 15),
            27 at LocalDateTime(2020, 10, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        with(result) {
            entries.size shouldBe 3
            labels[0] shouldBe "11/10 - 17/10"
            labels[1] shouldBe "18/10 - 24/10"
            labels[2] shouldBe "25/10 - 31/10"
        }
    }

    @Test
    fun `returns the appropriate entries for the monthly history`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Monthly.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            11 at LocalDateTime(2020, 11, 11, 1, 15),
            21 at LocalDateTime(2020, 12, 21, 1, 15),
            27 at LocalDateTime(2021, 1, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        with(result) {
            entries.size shouldBe 3
            entries[0].y.toDouble() shouldBeCloseTo 11.0
            entries[1].y.toDouble() shouldBeCloseTo 21.0
            entries[2].y.toDouble() shouldBeCloseTo 27.0
        }
    }

    @Test
    fun `returns the appropriate labels for the monthly history`() = runBlocking {
        // Arrange
        coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Monthly.toRight()
        coEvery { getCurrencyPreferenceMock() } returns USD.toRight()
        val prices = listOf(
            11 at LocalDateTime(2020, 11, 11, 1, 15),
            21 at LocalDateTime(2020, 12, 21, 1, 15),
            27 at LocalDateTime(2021, 1, 26, 1, 15)
        )

        // Act
        val result = sut.map(prices)

        // Assert
        with(result) {
            entries.size shouldBe 3
            labels[0] shouldBe "Nov 2020"
            labels[1] shouldBe "Dec 2020"
            labels[2] shouldBe "Jan 2021"
        }
    }

    @Test
    fun `returns an empty list of labels and entries when the price history is empty`() =
        runBlocking {
            // Arrange
            coEvery { getTimeUnitPreferenceMock(Unit) } returns TimeGranularity.Monthly.toRight()
            coEvery { getCurrencyPreferenceMock() } returns USD.toRight()

            // Act
            val result = sut.map(emptyList())

            // Assert
            with(result) {
                labels.size shouldBe 0
                entries.size shouldBe 0
            }
        }

    //region Helpers
    private fun configureDefaultMocks() {
        every { dateTimePatternProvider.generatePattern(any(), any()) } returns "dd/MM"
    }
    //endregion
}