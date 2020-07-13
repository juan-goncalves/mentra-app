package me.juangoncalves.mentra.domain.usecases

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.Ripple
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCoinsUseCaseTest {

    @MockK lateinit var coinRepositoryMock: CoinRepository

    private lateinit var getCoins: GetCoinsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getCoins = GetCoinsUseCase(coinRepositoryMock)
    }

    @Test
    fun `should get coin list from the repository`() = runBlocking {
        // Arrange
        val coins = listOf(
            Bitcoin,
            Ethereum,
            Ripple
        )
        coEvery { coinRepositoryMock.getCoins() } returns Either.Right(coins)

        // Act
        val result = getCoins()

        // Assert
        coVerify { coinRepositoryMock.getCoins() }
        assertEquals(Either.Right(coins), result)
    }

}