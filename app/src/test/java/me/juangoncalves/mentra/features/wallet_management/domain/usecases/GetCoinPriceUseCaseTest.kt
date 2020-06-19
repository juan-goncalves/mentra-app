package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.core.errors.PriceNotFound
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Money
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetCoinPriceUseCaseTest {

    private lateinit var coinRepositoryMock: CoinRepository
    private lateinit var useCase: GetCoinPriceUseCase

    @Before
    fun setUp() {
        coinRepositoryMock = mock(CoinRepository::class.java)
        useCase = GetCoinPriceUseCase(coinRepositoryMock)
    }

    @Test
    fun `should return the price of the selected coin from the repository`() = runBlocking {
        // Arrange
        val dataStub = Either.Right(Money(Currency.USD, 9834.23))
        `when`(coinRepositoryMock.getCoinPrice(Bitcoin, Currency.USD)).thenReturn(dataStub)

        // Act
        val result = useCase.execute(Bitcoin) as Either.Right

        // Assert
        assertThat(result.value.value, closeTo(9834.23, 0.001))
        assertEquals(Currency.USD, result.value.currency)
    }

    @Test
    fun `should fail if the price of the selected coin is not found`() = runBlocking {
        // Arrange
        val failure = PriceNotFound(Ethereum)
        `when`(
            coinRepositoryMock.getCoinPrice(
                eq(Ethereum),
                any()
            )
        ).thenReturn(Either.Left(failure))

        // Act
        val result = useCase.execute(Ethereum) as Either.Left

        // Assert
        assertTrue(result.value is PriceNotFound)
        assertEquals((result.value as PriceNotFound).coin, Ethereum)
    }
}