package me.juangoncalves.mentra.ui.wallet_list.mappers

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.GetGradientCoinIcon
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UIWalletMapperTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    //endregion

    //region Mocks
    @MockK private lateinit var getGradientCoinIconMock: GetGradientCoinIcon
    //endregion

    private lateinit var sut: UIWalletMapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sut = UIWalletMapper(mainCoroutineRule.dispatcher, getGradientCoinIconMock)
    }

    @Test
    fun `map correctly transforms a domain Wallet to a UI Wallet`() = runBlockingTest {
        // Arrange
        val wallet = Wallet(Bitcoin, 1.5, 10)
        val btcPrice = 12_431.0.toPrice()
        coEvery { getGradientCoinIconMock.invoke(any()) } returns Right("mock")

        // Act
        val result = sut.map(wallet, btcPrice)

        // Assert
        result.id equals 10
        result.amountOfCoin closeTo 1.5
        result.value closeTo 12_431.0 * 1.5
        result.primaryIconUrl equals "mock"
        result.secondaryIconUrl equals Bitcoin.imageUrl
        result.coin.name equals Bitcoin.name
        result.coin.value closeTo btcPrice.value
        coVerify { getGradientCoinIconMock.invoke(any()) }
    }

    @Test
    fun `map defines the primary icon url as an empty string if the use case fails`() =
        runBlockingTest {
            // Arrange
            val wallet = Wallet(Bitcoin, 1.0, 10)
            val btcPrice = 12_431.0.toPrice()
            coEvery { getGradientCoinIconMock.invoke(any()) } returns Left(mockk())

            // Act
            val result = sut.map(wallet, btcPrice)

            // Assert
            result.primaryIconUrl equals ""
        }

    //region Helpers
    //endregion

}