package me.juangoncalves.mentra.domain.usecases.portfolio

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class GetPortfolioDistributionStreamTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK private lateinit var portfolioRepositoryMock: PortfolioRepository
    @MockK private lateinit var walletRepositoryMock: WalletRepository
    //endregion

    private lateinit var sut: GetPortfolioDistributionStream

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sut = GetPortfolioDistributionStream(portfolioRepositoryMock, walletRepositoryMock)
    }

    @Test
    fun `emits the appropriate percentages when the portfolio value changes`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(500.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns wallets.toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[0]) } returns listOf(
            Price(Currency.USD, 300.0, days[0]),
            Price(Currency.USD, 150.0, days[1])
        ).toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[1]) } returns listOf(
            Price(Currency.USD, 50.0, days[0]),
            Price(Currency.USD, 40.0, days[1])
        ).toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[2]) } returns listOf(
            Price(Currency.USD, 10.0, days[0]),
            Price(Currency.USD, 15.0, days[1])
        ).toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[3]) } returns listOf(
            Price(Currency.USD, 140.0, days[0]),
            Price(Currency.USD, 60.0, days[1])
        ).toRight()

        // Act
        val result = sut().first()

        // Assert
        result.getOrDefault(Bitcoin, -1.0) closeTo 0.88
        result.getOrDefault(Ethereum, -1.0) closeTo 0.1
        result.getOrDefault(Ripple, -1.0) closeTo 0.02
    }

    @Test
    fun `assigns 0% to coins that don't have any values`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(300.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns wallets.subList(0, 2).toRight()
        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[1]) } returns Left(mockk())
        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[0]) } returns listOf(
            Price(Currency.USD, 300.0, days[0]),
            Price(Currency.USD, 150.0, days[1])
        ).toRight()

        // Act
        val result = sut().first()

        // Assert
        result.getOrDefault(Bitcoin, -1.0) closeTo 1.0
        result.getOrDefault(Ethereum, -1.0) closeTo 0.0
    }

    @Test
    fun `emits an empty map when there are no wallets`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(0.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns emptyList<Wallet>().toRight()

        // Act
        val result = sut().first()

        // Assert
        result.size equals 0
    }

    @Test
    fun `emits an empty map when the portfolio value is 0`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(0.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns wallets.subList(0, 1).toRight()
        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[0]) } returns listOf(
            Price(Currency.USD, 0.0, days[0])
        ).toRight()

        // Act
        val result = sut().first()

        // Assert
        result.size equals 0
    }

    @Test
    fun `emits an empty map when the wallet fetch fails`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(0.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns Left(mockk())

        // Act
        val result = sut().first()

        // Assert
        result.size equals 0
    }

    //region Helpers
    private val wallets = listOf(
        Wallet(Bitcoin, 0.5, 1),
        Wallet(Ethereum, 1.2, 2),
        Wallet(Ripple, 50.0, 3),
        Wallet(Bitcoin, 0.2, 10)
    )

    private val days = listOf(
        LocalDateTime.of(2020, 10, 17, 1, 30),
        LocalDateTime.of(2020, 10, 16, 12, 30)
    )
    //endregion

}