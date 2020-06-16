package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
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
        val coins = listOf(
            Coin(name = "Bitcoin", symbol = "BTC", imageUrl = "http://url.com/btc.jpg"),
            Coin(name = "Ethereum", symbol = "ETH", imageUrl = "http://url.com/eth.jpg"),
            Coin(name = "Ripple", symbol = "XRP", imageUrl = "http://url.com/xrp.jpg")
        )
        `when`(coinRepositoryMock.getCoins()).thenReturn(Either.Right(coins))

        // Assess
        val result = useCase.execute()

        // Assert
        assertEquals(Either.Right(coins), result)
        verify(coinRepositoryMock).getCoins()
    }

}
