package me.juangoncalves.mentra.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.requireRight
import me.juangoncalves.mentra.extensions.rightValue
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetPortfolioValueHistoryUseCaseTest {

    @MockK private lateinit var walletRepositoryMock: WalletRepository

    private lateinit var sut: GetPortfolioValueHistoryUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = GetPortfolioValueHistoryUseCase(walletRepositoryMock)
    }

    @Test
    fun `groups the wallet values per day correctly`() = runBlocking {
        // Arrange
        val (btcWallet, btcHist) = fakeBitcoinWallet()
        val (ethWallet, ethHist) = fakeEthereumWallet()
        val (xrpWallet, xrpHist) = fakeRippleWallet()
        val wallets = listOf(btcWallet, ethWallet, xrpWallet)

        coEvery { walletRepositoryMock.getWallets() } returns Right(wallets)
        coEvery { walletRepositoryMock.getWalletValueHistory(btcWallet) } returns Right(btcHist)
        coEvery { walletRepositoryMock.getWalletValueHistory(ethWallet) } returns Right(ethHist)
        coEvery { walletRepositoryMock.getWalletValueHistory(xrpWallet) } returns Right(xrpHist)

        // Act
        val result = sut()

        // Assert
        assertNotNull(result.rightValue)
        val valueMap = result.requireRight()
        assertThat(valueMap[day1.toLocalDate()], closeTo(1176.67, 0.0001))
        assertThat(valueMap[day2.toLocalDate()], closeTo(1358.23, 0.0001))
        assertThat(valueMap[day3.toLocalDate()], closeTo(1177.00, 0.0001))
        assertThat(valueMap[day4.toLocalDate()], closeTo(1743.00, 0.0001))
    }

    @Test
    fun `returns a failure if the wallets can't be retrieved`() = runBlocking {
        // Arrange
        coEvery { walletRepositoryMock.getWallets() } returns Left(StorageFailure())

        // Act
        val result = sut()

        // Assert
        assertNotNull(result.leftValue)
    }

    @Test
    fun `returns a failure if any of the wallet value histories can't be retrieved`() =
        runBlocking {
            // Arrange
            val (btcWallet, _) = fakeBitcoinWallet()
            val wallets = listOf(btcWallet)

            coEvery { walletRepositoryMock.getWallets() } returns Right(wallets)
            coEvery {
                walletRepositoryMock.getWalletValueHistory(any())
            } returns Left(StorageFailure())

            // Act
            val result = sut()

            // Assert
            assertNotNull(result.leftValue)
        }

    private fun fakeBitcoinWallet(): Pair<Wallet, List<Price>> {
        val wallet = Wallet(Bitcoin, 1.225, 1)
        val valueHistory = listOf(
            Price(Currency.USD, 1000.0, day1),
            Price(Currency.USD, 1200.0, day2),
            Price(Currency.USD, 900.0, day3),
            Price(Currency.USD, 1500.0, day4)
        )
        return Pair(wallet, valueHistory)
    }

    private fun fakeEthereumWallet(): Pair<Wallet, List<Price>> {
        val wallet = Wallet(Ethereum, 0.45, 2)
        val valueHistory = listOf(
            Price(Currency.USD, 123.0, day1),
            Price(Currency.USD, 98.0, day2),
            Price(Currency.USD, 230.0, day3),
            Price(Currency.USD, 180.0, day4)
        )
        return Pair(wallet, valueHistory)
    }

    private fun fakeRippleWallet(): Pair<Wallet, List<Price>> {
        val wallet = Wallet(Ripple, 53.67, 3)
        val valueHistory = listOf(
            Price(Currency.USD, 53.67, day1),
            Price(Currency.USD, 60.23, day2),
            Price(Currency.USD, 47.0, day3),
            Price(Currency.USD, 63.0, day4)
        )
        return Pair(wallet, valueHistory)
    }

    private val day1 = LocalDateTime.of(2020, 9, 7, 3, 34)
    private val day2 = LocalDateTime.of(2020, 9, 8, 5, 10)
    private val day3 = LocalDateTime.of(2020, 9, 9, 10, 0)
    private val day4 = LocalDateTime.of(2020, 9, 10, 17, 14)

}