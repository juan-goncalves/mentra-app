package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.Ripple
import me.juangoncalves.mentra.features.wallet_management.USDPrices
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Wallet
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class GetPortfolioValueUseCaseTest {

    private lateinit var getWalletsUseCaseMock: GetWalletsUseCase
    private lateinit var getCoinPriceUseCaseMock: GetCoinPriceUseCase
    private lateinit var useCase: GetPortfolioValueUseCase

    @Before
    fun setUp() {
        getWalletsUseCaseMock = mock(GetWalletsUseCase::class.java)
        getCoinPriceUseCaseMock = mock(GetCoinPriceUseCase::class.java)
        useCase = GetPortfolioValueUseCase(getWalletsUseCaseMock, getCoinPriceUseCaseMock)
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

        `when`(getWalletsUseCaseMock.execute()).thenReturn(Either.Right(wallets))

        `when`(getCoinPriceUseCaseMock.execute(Bitcoin))
            .thenReturn(Either.Right(USDPrices.getValue(Bitcoin)))

        `when`(getCoinPriceUseCaseMock.execute(Ethereum))
            .thenReturn(Either.Right(USDPrices.getValue(Ethereum)))

        `when`(getCoinPriceUseCaseMock.execute(Ripple))
            .thenReturn(Either.Right(USDPrices.getValue(Ripple)))

        // Act
        val result = useCase.execute(currency = Currency.USD) as Either.Right

        // Assess
        val resultData = result.value
        assertEquals(Currency.USD, resultData.currency)
        assertThat(resultData.value, closeTo(5870.4863, 0.0001))
    }

    @Test
    fun `should return a failure if there's and error obtaining the wallets`() = runBlocking {
        // Arrange
        `when`(getWalletsUseCaseMock.execute()).thenReturn(Either.Left(mock(Failure::class.java)))

        // Act
        val result = useCase.execute()

        // Assess
        assertTrue(result is Either.Left<Failure>)
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

        `when`(getWalletsUseCaseMock.execute()).thenReturn(Either.Right(wallets))

        `when`(getCoinPriceUseCaseMock.execute(Bitcoin))
            .thenReturn(Either.Left(mock(Failure::class.java)))

        `when`(getCoinPriceUseCaseMock.execute(Ethereum))
            .thenReturn(Either.Right(USDPrices.getValue(Ethereum)))

        `when`(getCoinPriceUseCaseMock.execute(Ripple))
            .thenReturn(Either.Right(USDPrices.getValue(Ripple)))

        // Act
        val result = useCase.execute(currency = Currency.USD) as Either.Right

        // Assess
        val resultData = result.value
        assertEquals(Currency.USD, resultData.currency)
        assertThat(resultData.value, closeTo(326.2779, 0.0001))
    }

}