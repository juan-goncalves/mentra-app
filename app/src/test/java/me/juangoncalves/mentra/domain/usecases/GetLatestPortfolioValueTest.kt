package me.juangoncalves.mentra.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.USDPrices
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.requireRight
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class GetLatestPortfolioValueTest {

    @MockK lateinit var portfolioRepositoryMock: PortfolioRepository

    private lateinit var getLatestPortfolioValue: GetLatestPortfolioValue

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getLatestPortfolioValue = GetLatestPortfolioValue(portfolioRepositoryMock)
    }

    @Test
    fun `should return the value provided by the portfolio repository`() = runBlocking {
        // Arrange
        coEvery { portfolioRepositoryMock.getLatestPortfolioValue() } returns Right(USDPrices[Bitcoin])

        // Act
        val result = getLatestPortfolioValue()

        // Assert
        val priceResult = result.requireRight()
        assertEquals(Currency.USD, priceResult!!.currency)
        assertThat(priceResult.value, closeTo(9538.423, 0.0001))
    }

    @Test
    fun `should return a failure if the repository value fetch fails`() = runBlocking {
        // Arrange
        coEvery { portfolioRepositoryMock.getLatestPortfolioValue() } returns Left(StorageFailure())

        // Act
        val result = getLatestPortfolioValue()

        // Assert
        assertNotNull(result.leftValue)
    }


}