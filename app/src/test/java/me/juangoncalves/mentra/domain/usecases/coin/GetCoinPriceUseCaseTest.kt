package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.domain.errors.PriceNotFound
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetCoinPriceUseCaseTest {

    @MockK lateinit var coinRepositoryMock: CoinRepository

    private lateinit var getCoinPrice: GetCoinPriceUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getCoinPrice =
            GetCoinPriceUseCase(
                coinRepositoryMock
            )
    }

    @Test
    fun `should return the price of the selected coin from the repository`() = runBlocking {
        // Arrange
        val fakeResult = Right(Price(Currency.USD, 9834.23, LocalDateTime.now()))
        coEvery { coinRepositoryMock.getCoinPrice(Bitcoin, Currency.USD) } returns fakeResult

        // Act
        val result = getCoinPrice(Bitcoin)

        // Assert
        val resultData = (result as Right).value
        assertThat(resultData.value, closeTo(9834.23, 0.001))
        assertEquals(Currency.USD, result.value.currency)
    }

    @Test
    fun `should fail if the price of the selected coin is not found`() = runBlocking {
        // Arrange
        val failure = PriceNotFound(Ethereum)
        coEvery { coinRepositoryMock.getCoinPrice(Ethereum, any()) } returns Left(failure)

        // Act
        val result = getCoinPrice(Ethereum) as Either.Left

        // Assert
        assertTrue(result.value is PriceNotFound)
        assertEquals((result.value as PriceNotFound).coin, Ethereum)
    }
}