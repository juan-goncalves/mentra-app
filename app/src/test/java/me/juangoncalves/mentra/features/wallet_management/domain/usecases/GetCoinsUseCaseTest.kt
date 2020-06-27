package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.Ripple
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCoinsUseCaseTest {

    @MockK lateinit var coinRepositoryMock: CoinRepository

    private lateinit var useCase: GetCoinsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetCoinsUseCase(coinRepositoryMock)
    }

    @Test
    fun `should get coin list from the repository`() = runBlocking {
        // Arrange
        val coins = listOf(Bitcoin, Ethereum, Ripple)
        coEvery { coinRepositoryMock.getCoins() } returns Either.Right(coins)

        // Act
        val result = useCase.execute()

        // Assert
        coVerify { coinRepositoryMock.getCoins() }
        assertEquals(Either.Right(coins), result)
    }

}
