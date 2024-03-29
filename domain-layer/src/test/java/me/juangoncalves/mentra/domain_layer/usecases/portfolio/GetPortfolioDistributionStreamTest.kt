package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.Bitcoin
import me.juangoncalves.mentra.domain_layer.Ethereum
import me.juangoncalves.mentra.domain_layer.Ripple
import me.juangoncalves.mentra.domain_layer.extensions.Left
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.toPrice
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Test

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
            300.0.toPrice(timestamp = days[0]),
            150.0.toPrice(timestamp = days[1])
        ).toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[1]) } returns listOf(
            50.0.toPrice(timestamp = days[0]),
            40.0.toPrice(timestamp = days[1])
        ).toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[2]) } returns listOf(
            10.0.toPrice(timestamp = days[0]),
            15.0.toPrice(timestamp = days[1])
        ).toRight()

        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[3]) } returns listOf(
            140.0.toPrice(timestamp = days[0]),
            60.0.toPrice(timestamp = days[1])
        ).toRight()

        // Act
        val result = sut().first()

        // Assert
        result.getOrDefault(Bitcoin, -1.0) shouldBeCloseTo 0.88
        result.getOrDefault(Ethereum, -1.0) shouldBeCloseTo 0.1
        result.getOrDefault(Ripple, -1.0) shouldBeCloseTo 0.02
    }

    @Test
    fun `assigns 0 to coins that don't have any values`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(300.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns wallets.subList(0, 2).toRight()
        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[1]) } returns Left(mockk())
        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[0]) } returns listOf(
            300.0.toPrice(timestamp = days[0]),
            150.0.toPrice(timestamp = days[1])
        ).toRight()

        // Act
        val result = sut().first()

        // Assert
        result.getOrDefault(Bitcoin, -1.0) shouldBeCloseTo 1.0
        result.getOrDefault(Ethereum, -1.0) shouldBeCloseTo 0.0
    }

    @Test
    fun `emits an empty map when there are no wallets`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(0.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns emptyList<Wallet>().toRight()

        // Act
        val result = sut().first()

        // Assert
        result.size shouldBe 0
    }

    @Test
    fun `emits an empty map when the portfolio value is 0`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(0.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns wallets.subList(0, 1).toRight()
        coEvery { walletRepositoryMock.getWalletValueHistory(wallets[0]) } returns listOf(
            0.0.toPrice(timestamp = days[0])
        ).toRight()

        // Act
        val result = sut().first()

        // Assert
        result.size shouldBe 0
    }

    @Test
    fun `emits an empty map when the wallet fetch fails`() = runBlockingTest {
        // Arrange
        coEvery { portfolioRepositoryMock.portfolioValue } returns flowOf(0.0.toPrice())
        coEvery { walletRepositoryMock.getWallets() } returns Left(mockk())

        // Act
        val result = sut().first()

        // Assert
        result.size shouldBe 0
    }

    //region Helpers
    private val wallets = listOf(
        Wallet(Bitcoin, 0.5, 1),
        Wallet(Ethereum, 1.2, 2),
        Wallet(Ripple, 50.0, 3),
        Wallet(Bitcoin, 0.2, 10)
    )

    private val days = listOf(
        LocalDateTime(2020, 10, 17, 1, 30),
        LocalDateTime(2020, 10, 16, 12, 30)
    )
    //endregion

}