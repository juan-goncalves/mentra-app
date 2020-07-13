package me.juangoncalves.mentra.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.PriceNotFound
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Wallet
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class GetPortfolioValueUseCaseTest {

    @MockK lateinit var getWalletsMock: GetWalletsUseCase
    @MockK lateinit var getCoinPriceMock: GetCoinPriceUseCase

    private lateinit var getPortfolioValue: GetPortfolioValueUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getPortfolioValue = GetPortfolioValueUseCase(getWalletsMock, getCoinPriceMock)
    }

    @Test
    fun `should return the sum of all the current values of the available wallets`() = runBlocking {
        // Arrange
        val wallets = listOf(
            Wallet(1, "BTC #1", Bitcoin, 0.5678),
            Wallet(2, "ETH fav", Ethereum, 1.321),
            Wallet(3, "BTC second", Bitcoin, 0.01345),
            Wallet(4, "Ripple!", Ripple, 20.53)
        )
        coEvery { getWalletsMock() } returns Right(wallets)
        coEvery { getCoinPriceMock(Bitcoin) } returns Right(USDPrices[Bitcoin]!!)
        coEvery { getCoinPriceMock(Ethereum) } returns Right(USDPrices[Ethereum]!!)
        coEvery { getCoinPriceMock(Ripple) } returns Right(USDPrices[Ripple]!!)

        // Act
        val result = getPortfolioValue(currency = Currency.USD)

        // Assert
        val resultData = (result as Right).value
        assertEquals(Currency.USD, resultData.currency)
        assertThat(resultData.value, closeTo(5870.4863, 0.0001))
    }

    @Test
    fun `should return a failure if there's and error obtaining the wallets`() = runBlocking {
        // Arrange
        coEvery { getWalletsMock() } returns Left(mockk())

        // Act
        val result = getPortfolioValue()

        // Assert
        assertTrue(result is Left<Failure>)
    }

    @Test
    fun `should ignore the wallet if its coin price isn't found`() = runBlocking {
        // Arrange
        val wallets = listOf(
            Wallet(1, "BTC #1", Bitcoin, 0.5678),
            Wallet(2, "ETH fav", Ethereum, 1.321),
            Wallet(3, "BTC second", Bitcoin, 0.01345),
            Wallet(4, "Ripple!", Ripple, 20.53)
        )
        coEvery { getWalletsMock() } returns Right(wallets)
        coEvery { getCoinPriceMock(Bitcoin) } returns Left(PriceNotFound(Bitcoin))
        coEvery { getCoinPriceMock(Ethereum) } returns Right(USDPrices[Ethereum]!!)
        coEvery { getCoinPriceMock(Ripple) } returns Right(USDPrices[Ripple]!!)

        // Act
        val result = getPortfolioValue(currency = Currency.USD)

        // Assert
        val resultData = (result as Right).value
        assertEquals(Currency.USD, resultData.currency)
        assertThat(resultData.value, closeTo(326.2779, 0.0001))
    }

}