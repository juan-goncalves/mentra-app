package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Wallet
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.WalletRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetWalletsUseCaseTest {

    private lateinit var walletRepositoryMock: WalletRepository
    private lateinit var userCase: GetWalletsUseCase

    @Before
    fun setUp() {
        walletRepositoryMock = mock(WalletRepository::class.java)
        userCase = GetWalletsUseCase(walletRepositoryMock)
    }

    @Test
    fun `should return wallets from the repository`() = runBlocking {
        // Arrange
        val walletStubs = listOf(
            Wallet(9231, "BTC #1", Bitcoin, 1.32),
            Wallet(1431, "BTC #2", Bitcoin, 0.5543),
            Wallet(56, "Fav. Ethereum", Ethereum, 0.32)
        )
        `when`(walletRepositoryMock.getWallets()).thenReturn(Either.Right(walletStubs))

        // Act
        val result = userCase.execute()

        // Assert
        verify(walletRepositoryMock).getWallets()
        assertEquals(result, Either.Right(walletStubs))
    }

}