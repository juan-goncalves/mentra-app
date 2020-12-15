package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.domain_layer.at
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity.*
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.shouldBe
import me.juangoncalves.mentra.domain_layer.shouldBeLessThanOrEqualTo
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class GetPortfolioValueHistoryStreamTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var portfolioRepositoryMock: PortfolioRepository
    @MockK lateinit var prefRepositoryMock: PreferenceRepository
    //endregion

    private lateinit var sut: GetPortfolioValueHistoryStream

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = GetPortfolioValueHistoryStream(
            portfolioRepositoryMock,
            prefRepositoryMock
        )
    }

    @Test
    fun `emits the daily price history when the preference is set to Daily`() =
        runBlocking {
            // Arrange
            val prices = listOf(
                10 at LocalDateTime.of(2020, 10, 20, 10, 23),
                15 at LocalDateTime.of(2020, 10, 21, 7, 12),
                13 at LocalDateTime.of(2020, 10, 22, 22, 23),
                17 at LocalDateTime.of(2020, 10, 23, 1, 15)
            )
            coEvery { portfolioRepositoryMock.portfolioDailyValueHistory } returns flowOf(prices)
            coEvery { prefRepositoryMock.valueChartTimeUnitStream } returns flowOf(Daily)

            // Act
            val result = sut().single()

            // Assert
            result shouldBe prices
        }

    @Test
    fun `builds the weekly history using the week's latest day value`() =
        runBlocking {
            // Arrange
            val prices = listOf(
                // Week 1
                11 at LocalDateTime.of(2020, 10, 11, 10, 23),
                12 at LocalDateTime.of(2020, 10, 12, 7, 12),
                16 at LocalDateTime.of(2020, 10, 16, 22, 23),
                // Week 2
                19 at LocalDateTime.of(2020, 10, 19, 10, 23),
                23 at LocalDateTime.of(2020, 10, 23, 7, 12),
                // Week 3
                25 at LocalDateTime.of(2020, 10, 25, 22, 23),
                27 at LocalDateTime.of(2020, 10, 27, 22, 23)
            )
            coEvery { portfolioRepositoryMock.portfolioDailyValueHistory } returns flowOf(prices)
            coEvery { prefRepositoryMock.valueChartTimeUnitStream } returns flowOf(Weekly)

            // Act
            val result = sut().single()

            // Assert
            result[0] shouldBe prices[2]
            result[1] shouldBe prices[4]
            result[2] shouldBe prices[6]
        }

    @Test
    fun `builds the monthly history using the month's latest day value`() =
        runBlocking {
            // Arrange
            val prices = listOf(
                // Month 1
                11 at LocalDateTime.of(2020, 11, 1, 10, 23),
                12 at LocalDateTime.of(2020, 11, 7, 7, 12),
                16 at LocalDateTime.of(2020, 11, 24, 22, 23),
                // Month 2
                19 at LocalDateTime.of(2020, 12, 19, 10, 23),
                23 at LocalDateTime.of(2020, 12, 23, 7, 12),
                // Month 3
                25 at LocalDateTime.of(2021, 1, 25, 22, 23),
                27 at LocalDateTime.of(2021, 1, 27, 22, 23)
            )
            coEvery { portfolioRepositoryMock.portfolioDailyValueHistory } returns flowOf(prices)
            coEvery { prefRepositoryMock.valueChartTimeUnitStream } returns flowOf(Monthly)

            // Act
            val result = sut().single()

            // Assert
            result[0] shouldBe prices[2]
            result[1] shouldBe prices[4]
            result[2] shouldBe prices[6]
        }

    @Test
    fun `emits a new price history when the time unit preference changes`() =
        runBlocking {
            // Arrange
            val prices = listOf(
                10 at LocalDateTime.of(2020, 10, 20, 10, 23),
                15 at LocalDateTime.of(2020, 10, 21, 7, 12),
                13 at LocalDateTime.of(2020, 10, 22, 22, 23)
            )
            coEvery { portfolioRepositoryMock.portfolioDailyValueHistory } returns flowOf(prices)
            coEvery { prefRepositoryMock.valueChartTimeUnitStream } returns flow {
                emit(Daily)
                emit(Monthly)
            }

            // Act
            val result = sut()

            // Assert
            result.collectIndexed { index, _ ->
                index + 1 shouldBeLessThanOrEqualTo 2
            }
        }

    @Test
    fun `ignores time unit updates if the actual value does not change`() =
        runBlocking {
            // Arrange
            val prices = listOf(
                10 at LocalDateTime.of(2020, 10, 20, 10, 23),
                15 at LocalDateTime.of(2020, 10, 21, 7, 12),
                13 at LocalDateTime.of(2020, 10, 22, 22, 23)
            )
            coEvery { portfolioRepositoryMock.portfolioDailyValueHistory } returns flowOf(prices)
            coEvery { prefRepositoryMock.valueChartTimeUnitStream } returns flow {
                emit(Daily)
                emit(Daily)
            }

            // Act
            val result = sut()

            // Assert
            result.collectIndexed { index, _ ->
                index + 1 shouldBeLessThanOrEqualTo 1
            }
        }

    //region Helpers
    //endregion

}