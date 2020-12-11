package me.juangoncalves.mentra.features.wallet_list.mappers

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.DeterminePrimaryIcon
import me.juangoncalves.mentra.domain.usecases.currency.ExchangePriceToPreferredCurrency
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WalletMapperTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK private lateinit var determinePrimaryIconMock: DeterminePrimaryIcon
    @MockK private lateinit var exchangePriceToPreferredCurrency: ExchangePriceToPreferredCurrency
    //endregion

    private lateinit var sut: WalletMapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sut = WalletMapper(determinePrimaryIconMock, exchangePriceToPreferredCurrency)
    }

    @Test
    fun `map correctly transforms a domain Wallet to a UI Wallet`() = runBlocking {
        // Arrange
        val wallet = Wallet(Bitcoin, 1.5, 10)
        val btcPrice = 12_431.0.toPrice()
        coEvery { determinePrimaryIconMock.invoke(any()) } returns Right("mock")
        coEvery { exchangePriceToPreferredCurrency.execute(btcPrice) } returns btcPrice

        // Act
        val result = sut.map(wallet, btcPrice)

        // Assert
        coVerify { determinePrimaryIconMock.invoke(any()) }
        with(result) {
            id shouldBe 10
            amountOfCoin shouldBeCloseTo 1.5
            value.value shouldBeCloseTo 12_431.0 * 1.5
            iconUrl shouldBe "mock"
            coin.name shouldBe Bitcoin.name
            coin.value.value shouldBeCloseTo btcPrice.value
        }
    }

    @Test
    fun `map defines the primary icon url as an empty string if the use case fails`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 1.0, 10)
            val btcPrice = 12_431.0.toPrice()
            coEvery { determinePrimaryIconMock.invoke(any()) } returns Left(mockk())
            coEvery { exchangePriceToPreferredCurrency.execute(btcPrice) } returns btcPrice

            // Act
            val result = sut.map(wallet, btcPrice)

            // Assert
            result.iconUrl shouldBe ""
        }

    //region Helpers
    //endregion

}