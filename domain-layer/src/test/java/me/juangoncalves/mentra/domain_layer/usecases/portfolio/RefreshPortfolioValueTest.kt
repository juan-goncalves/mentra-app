package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.domain_layer.*
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.wallet.RefreshWalletValue
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class RefreshPortfolioValueTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var walletRepoMock: WalletRepository
    @MockK lateinit var portfolioRepoMock: PortfolioRepository
    @MockK lateinit var coinRepoMock: CoinRepository
    @MockK lateinit var refreshWalletValueMock: RefreshWalletValue
    //endregion

    private lateinit var sut: RefreshPortfolioValue

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = RefreshPortfolioValue(
            walletRepoMock,
            portfolioRepoMock,
            coinRepoMock,
            refreshWalletValueMock
        )

        coEvery { coinRepoMock.getCoinPrices(any()) } returns emptyMap<Coin, Price>().toRight()
        coEvery { portfolioRepoMock.portfolioValue } returns flowOf(null)
    }

    @Test
    fun `returns a Failure when there's an error getting the list of wallets`() = runBlocking {
        // Arrange
        coEvery { walletRepoMock.getWallets() } returns Failure.Unknown.toLeft()

        // Act
        val result = sut.invoke()

        // Assert
        result.leftValue shouldBeA Failure::class
    }

    @Test
    fun `returns a Failure if there's an error getting the latest value of any of the wallets`() =
        runBlocking {
            // Arrange
            coEvery { walletRepoMock.getWallets() } returns wallets.toRight()
            coEvery { refreshWalletValueMock.invoke(btcWallet) } returns 10.0.toPrice().toRight()
            coEvery { refreshWalletValueMock.invoke(ethWallet) } returns Failure.Unknown.toLeft()
            coEvery { refreshWalletValueMock.invoke(xrpWallet) } returns 15.0.toPrice().toRight()

            // Act
            val result = sut.invoke()

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    @Test
    fun `returns the total portfolio value in USD`() = runBlocking {
        // Arrange
        coEvery { walletRepoMock.getWallets() } returns wallets.toRight()
        coEvery { portfolioRepoMock.updatePortfolioUsdValue(any()) } returns Unit.toRight()
        coEvery { refreshWalletValueMock.invoke(btcWallet) } returns 28_000.0.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(ethWallet) } returns 303.2.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(xrpWallet) } returns 15.0.toPrice().toRight()

        // Act
        val result = sut.invoke()

        // Assert
        result.requireRight().currency shouldBe USD
    }

    @Test
    fun `calculates the total portfolio value correctly`() = runBlocking {
        // Arrange
        coEvery { walletRepoMock.getWallets() } returns wallets.toRight()
        coEvery { portfolioRepoMock.updatePortfolioUsdValue(any()) } returns Unit.toRight()
        coEvery { refreshWalletValueMock.invoke(btcWallet) } returns 28_000.0.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(ethWallet) } returns 303.2.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(xrpWallet) } returns 15.0.toPrice().toRight()

        // Act
        val result = sut.invoke()

        // Assert
        result.requireRight().value shouldBeCloseTo 28_318.2
    }

    @Test
    fun `updates the stored portfolio value`() = runBlocking {
        // Arrange
        val slot = slot<BigDecimal>()
        coEvery { walletRepoMock.getWallets() } returns wallets.toRight()
        coEvery { portfolioRepoMock.updatePortfolioUsdValue(capture(slot)) } returns Unit.toRight()
        coEvery { refreshWalletValueMock.invoke(btcWallet) } returns 28_000.0.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(ethWallet) } returns 303.2.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(xrpWallet) } returns 15.0.toPrice().toRight()

        // Act
        sut.invoke()

        // Assert
        coVerify { portfolioRepoMock.updatePortfolioUsdValue(any()) }
        slot.captured shouldBeCloseTo 28_318.2
    }

    @Test
    fun `returns a Failure if there's an error storing the portfolio value`() = runBlocking {
        // Arrange
        coEvery { walletRepoMock.getWallets() } returns wallets.toRight()
        coEvery { portfolioRepoMock.updatePortfolioUsdValue(any()) } returns Failure.Unknown.toLeft()
        coEvery { refreshWalletValueMock.invoke(btcWallet) } returns 28_000.0.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(ethWallet) } returns 303.2.toPrice().toRight()
        coEvery { refreshWalletValueMock.invoke(xrpWallet) } returns 15.0.toPrice().toRight()

        // Act
        val result = sut.invoke()

        // Assert
        result.leftValue shouldBeA Failure::class
    }

    //region Helpers
    private val btcWallet = Wallet(Bitcoin, 1.0.toBigDecimal(), 1)
    private val ethWallet = Wallet(Ethereum, 0.23.toBigDecimal(), 2)
    private val xrpWallet = Wallet(Ripple, 15.0.toBigDecimal(), 3)
    private val wallets = listOf(btcWallet, ethWallet, xrpWallet)
    //endregion

}