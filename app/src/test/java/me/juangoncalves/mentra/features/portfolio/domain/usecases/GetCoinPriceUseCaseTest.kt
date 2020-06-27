package me.juangoncalves.mentra.features.portfolio.domain.usecases

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.core.errors.PriceNotFound
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import me.juangoncalves.mentra.features.portfolio.domain.repositories.CoinRepository
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetCoinPriceUseCaseTest {

    @MockK lateinit var coinRepositoryMock: CoinRepository

    private lateinit var useCase: GetCoinPriceUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetCoinPriceUseCase(coinRepositoryMock)
    }

    @Test
    fun `should return the price of the selected coin from the repository`() = runBlocking {
        // Arrange
        val dataStub = Right(
            Price(
                Currency.USD,
                9834.23,
                LocalDateTime.now()
            )
        )
        coEvery { coinRepositoryMock.getCoinPrice(Bitcoin, Currency.USD) } returns dataStub

        // Act
        val result = useCase.execute(Bitcoin)

        // Assert
        val resultData = (result as Right).value
        assertThat(resultData.value, closeTo(9834.23, 0.001))
        assertEquals(Currency.USD, result.value.currency)
    }

    @Test
    fun `should fail if the price of the selected coin is not found`() = runBlocking {
        // Arrange
        val failure = PriceNotFound(Ethereum)
        coEvery { coinRepositoryMock.getCoinPrice(Ethereum, any()) } returns Left(
            failure
        )

        // Act
        val result = useCase.execute(Ethereum) as Either.Left

        // Assert
        assertTrue(result.value is PriceNotFound)
        assertEquals(
            (result.value as PriceNotFound).coin,
            Ethereum
        )
    }
}