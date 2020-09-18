package me.juangoncalves.mentra.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import org.hamcrest.CoreMatchers.isA
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class DeleteWalletUseCaseTest {

    @MockK lateinit var walletRepositoryMock: WalletRepository

    private lateinit var sut: DeleteWalletUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = DeleteWalletUseCase(walletRepositoryMock)
    }

    @Test
    fun `passes the received wallet to the repository`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ethereum, 12.321, 82)
        coEvery { walletRepositoryMock.deleteWallet(any()) } returns Right(Unit)

        // Act
        val result = sut(wallet)

        // Assert
        assertNotNull(result.rightValue)
        coVerify { walletRepositoryMock.deleteWallet(wallet) }
    }

    @Test
    fun `returns a failure if the operation fails`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ethereum, 12.321, 82)
        coEvery { walletRepositoryMock.deleteWallet(any()) } returns Left(StorageFailure())

        // Act
        val result = sut(wallet)

        // Assert
        assertNotNull(result.leftValue)
        assertThat(result.requireLeft(), isA(Failure::class.java))
    }

}