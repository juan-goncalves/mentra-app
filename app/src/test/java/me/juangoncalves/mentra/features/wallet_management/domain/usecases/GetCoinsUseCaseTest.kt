package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.Ripple
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetCoinsUseCaseTest {

    private lateinit var coinRepositoryMock: CoinRepository
    private lateinit var useCase: GetCoinsUseCase

    @Before
    fun setUp() {
        coinRepositoryMock = mock(CoinRepository::class.java)
        useCase = GetCoinsUseCase(coinRepositoryMock)
    }

    @Test
    fun `should get coin list from the repository`() = runBlocking<Unit> {
        // Arrange
        val coins = listOf(Bitcoin, Ethereum, Ripple)
        `when`(coinRepositoryMock.getCoins()).thenReturn(Either.Right(coins))

        // Act
        val result = useCase.execute()

        // Assert
        assertEquals(Either.Right(coins), result)
        verify(coinRepositoryMock).getCoins()
    }

}
