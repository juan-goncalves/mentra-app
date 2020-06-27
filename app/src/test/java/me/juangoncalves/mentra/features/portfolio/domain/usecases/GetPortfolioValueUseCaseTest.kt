package me.juangoncalves.mentra.features.portfolio.domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.core.errors.PriceNotFound
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Wallet
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class GetPortfolioValueUseCaseTest {

    @MockK lateinit var getWalletsUseCaseMock: GetWalletsUseCase
    @MockK lateinit var getCoinPriceUseCaseMock: GetCoinPriceUseCase

    private lateinit var useCase: GetPortfolioValueUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = GetPortfolioValueUseCase(getWalletsUseCaseMock, getCoinPriceUseCaseMock)
    }

    @Test
    fun `should return the sum of all the current values of the available wallets`() = runBlocking {
        // Arrange
        val wallets = listOf(
            Wallet(1, "BTC #1", Bitcoin, 0.5678),
            Wallet(2, "ETH fav", Ethereum, 1.321),
            Wallet(
                3, "BTC second",
                Bitcoin, 0.01345
            ),
            Wallet(4, "Ripple!", Ripple, 20.53)
        )
        coEvery { getWalletsUseCaseMock.execute() } returns Right(
            wallets
        )
        coEvery { getCoinPriceUseCaseMock.execute(Bitcoin) } returns Right(
            USDPrices[Bitcoin]!!
        )
        coEvery { getCoinPriceUseCaseMock.execute(Ethereum) } returns Right(
            USDPrices[Ethereum]!!
        )
        coEvery { getCoinPriceUseCaseMock.execute(Ripple) } returns Right(
            USDPrices[Ripple]!!
        )

        // Act
        val result = useCase.execute(currency = Currency.USD)

        // Assert
        val resultData = (result as Right).value
        assertEquals(Currency.USD, resultData.currency)
        assertThat(resultData.value, closeTo(5870.4863, 0.0001))
    }

    @Test
    fun `should return a failure if there's and error obtaining the wallets`() = runBlocking {
        // Arrange
        coEvery { getWalletsUseCaseMock.execute() } returns Left(
            mockk()
        )

        // Act
        val result = useCase.execute()

        // Assert
        assertTrue(result is Left<Failure>)
    }

    @Test
    fun `should ignore the wallet if its coin price isn't found`() = runBlocking {
        // Arrange
        val wallets = listOf(
            Wallet(1, "BTC #1", Bitcoin, 0.5678),
            Wallet(2, "ETH fav", Ethereum, 1.321),
            Wallet(
                3, "BTC second",
                Bitcoin, 0.01345
            ),
            Wallet(4, "Ripple!", Ripple, 20.53)
        )
        coEvery { getWalletsUseCaseMock.execute() } returns Right(
            wallets
        )
        coEvery { getCoinPriceUseCaseMock.execute(Bitcoin) } returns Left(
            PriceNotFound(Bitcoin)
        )
        coEvery { getCoinPriceUseCaseMock.execute(Ethereum) } returns Right(
            USDPrices[Ethereum]!!
        )
        coEvery { getCoinPriceUseCaseMock.execute(Ripple) } returns Right(
            USDPrices[Ripple]!!
        )

        // Act
        val result = useCase.execute(currency = Currency.USD)

        // Assert
        val resultData = (result as Right).value
        assertEquals(Currency.USD, resultData.currency)
        assertThat(resultData.value, closeTo(326.2779, 0.0001))
    }

}