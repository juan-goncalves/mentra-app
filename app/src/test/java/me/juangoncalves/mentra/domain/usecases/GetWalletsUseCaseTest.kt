package me.juangoncalves.mentra.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWalletsUseCaseTest {

    @MockK lateinit var walletRepositoryMock: WalletRepository

    private lateinit var getWallets: GetWalletsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getWallets = GetWalletsUseCase(walletRepositoryMock)
    }

    @Test
    fun `should return wallets from the repository`() = runBlocking {
        // Arrange
        val walletStubs = listOf(
            Wallet(Bitcoin, 1.32),
            Wallet(Bitcoin, 0.5543),
            Wallet(Ethereum, 0.32)
        )
        coEvery { walletRepositoryMock.getWallets() } returns Right(walletStubs)

        // Act
        val result = getWallets()

        // Assert
        assertEquals(result, Right(walletStubs))
        coVerify { walletRepositoryMock.getWallets() }
    }

}