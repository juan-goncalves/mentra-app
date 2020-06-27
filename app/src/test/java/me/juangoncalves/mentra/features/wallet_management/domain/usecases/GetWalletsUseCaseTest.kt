package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.Right
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Wallet
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.WalletRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWalletsUseCaseTest {

    @MockK lateinit var walletRepositoryMock: WalletRepository

    private lateinit var useCase: GetWalletsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetWalletsUseCase(walletRepositoryMock)
    }

    @Test
    fun `should return wallets from the repository`() = runBlocking {
        // Arrange
        val walletStubs = listOf(
            Wallet(9231, "BTC #1", Bitcoin, 1.32),
            Wallet(1431, "BTC #2", Bitcoin, 0.5543),
            Wallet(56, "Fav. Ethereum", Ethereum, 0.32)
        )
        coEvery { walletRepositoryMock.getWallets() } returns Right(walletStubs)

        // Act
        val result = useCase.execute()

        // Assert
        assertEquals(result, Right(walletStubs))
        coVerify { walletRepositoryMock.getWallets() }
    }

}