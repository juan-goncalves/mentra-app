package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import either.fold
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.core.errors.PriceNotFound
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.Ripple
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetCoinPricesUseCaseTest {

    private lateinit var coinRepositoryMock: CoinRepository
    private lateinit var useCase: GetCoinPricesUseCase

    @Before
    fun setUp() {
        coinRepositoryMock = mock(CoinRepository::class.java)
        useCase = GetCoinPricesUseCase(coinRepositoryMock)
    }

    @Test
    fun `should return the prices of the selected coins from the repository`() = runBlocking {
        // Arrange
        val prices = mapOf(
            Bitcoin to 9834.23,
            Ripple to 0.2987
        )
        `when`(
            coinRepositoryMock.getCoinPrices(
                eq(listOf(Bitcoin, Ripple)),
                any()
            )
        ).thenReturn(Either.Right(prices))

        // Act
        val result = useCase.execute(listOf(Bitcoin, Ripple))

        // Assess
        val resultData = result.fold(
            left = { throw IllegalStateException("For this test the result should be a Right") },
            right = { it }
        )
        assertTrue(resultData.containsKey(Bitcoin))
        assertTrue(resultData.containsKey(Ripple))
        assertThat(resultData[Bitcoin], closeTo(9834.23, 0.001))
        assertThat(resultData[Ripple], closeTo(0.2987, 0.001))
        assertEquals(2, resultData.size)
    }

    @Test
    fun `should fail if the price one of the selected coin is not found`() = runBlocking {
        // Arrange
        val failure = PriceNotFound(listOf(Ethereum))
        `when`(
            coinRepositoryMock.getCoinPrices(
                eq(listOf(Ethereum)),
                any()
            )
        ).thenReturn(Either.Left(failure))

        // Act
        val result = useCase.execute(listOf(Ethereum)) as Either.Left

        // Assess
        assertTrue(result.value is PriceNotFound)
        assertEquals((result.value as PriceNotFound).coins, listOf(Ethereum))
    }
}