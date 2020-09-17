package me.juangoncalves.mentra.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.InternetConnectionFailure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.requireRight
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class CalculatePortfolioDistributionUseCaseTest {

    @MockK private lateinit var getWalletsMock: GetWalletsUseCase
    @MockK private lateinit var refreshWalletValueMock: RefreshWalletValueUseCase

    private lateinit var sut: CalculatePortfolioDistributionUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = CalculatePortfolioDistributionUseCase(getWalletsMock, refreshWalletValueMock)
    }

    @Test
    fun `calculates the portions correctly`() = runBlocking {
        // Arrange
        val wallet1 = Wallet(Bitcoin, 0.15, 1)
        val wallet2 = Wallet(Ethereum, 0.45, 2)
        val wallet3 = Wallet(Ripple, 53.67, 3)
        val wallet4 = Wallet(Ripple, 22.0, 4)
        val wallets = listOf(wallet1, wallet3, wallet2, wallet4)

        coEvery { getWalletsMock.invoke() } returns Right(wallets)
        coEvery { refreshWalletValueMock(wallet1) } returns Right(1430.763.toPrice())
        coEvery { refreshWalletValueMock(wallet2) } returns Right(109.057.toPrice())
        coEvery { refreshWalletValueMock(wallet3) } returns Right(16.031.toPrice())
        coEvery { refreshWalletValueMock(wallet4) } returns Right(6.571.toPrice())

        // Act
        val result = sut()

        // Assert
        val portionMap = result.requireRight()
        assertThat(portionMap.values.sum(), closeTo(1.0, 0.01))
        assertThat(portionMap[Bitcoin], closeTo(0.91568, 0.01))
        assertThat(portionMap[Ethereum], closeTo(0.0697, 0.01))
        assertThat(portionMap[Ripple], closeTo(0.0145, 0.01))
    }

    @Test
    fun `returns a failure if the wallet fetch fails`() = runBlocking {
        // Arrange
        coEvery { getWalletsMock.invoke() } returns Left(StorageFailure())

        // Act
        val result = sut()

        // Assert
        assertNotNull(result.leftValue)
    }

    @Test
    fun `returns a failure if any the value of a wallet can't be found`() = runBlocking {
        // Arrange
        val wallet1 = Wallet(Bitcoin, 0.15, 1)
        val wallet2 = Wallet(Ethereum, 0.45, 2)
        val wallets = listOf(wallet1, wallet2)

        coEvery { getWalletsMock.invoke() } returns Right(wallets)
        coEvery { refreshWalletValueMock.invoke(any()) } returns Right(300.0.toPrice())
        coEvery { refreshWalletValueMock.invoke(wallet2) } returns Left(InternetConnectionFailure())

        // Act
        val result = sut()

        // Assert
        assertNotNull(result.leftValue)
    }

}