package me.juangoncalves.mentra.features.wallet_list.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.usecases.coin.GetActiveCoinsPriceStream
import me.juangoncalves.mentra.domain_layer.usecases.currency.ExchangePriceToPreferredCurrency
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetCurrencyPreferenceStream
import me.juangoncalves.mentra.domain_layer.usecases.wallet.GetWalletListStream
import me.juangoncalves.mentra.failures.FleetingError
import me.juangoncalves.mentra.features.common.Event
import me.juangoncalves.mentra.features.wallet_list.mappers.WalletMapper
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState.Error
import me.juangoncalves.mentra.test_utils.MainCoroutineRule
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class WalletListViewModelTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    @MockK lateinit var activeCoinsPriceStreamMock: GetActiveCoinsPriceStream
    @MockK lateinit var walletListStreamMock: GetWalletListStream
    @MockK lateinit var refreshPortfolioValueMock: RefreshPortfolioValue
    @MockK lateinit var stateObserver: Observer<WalletListViewState>
    @MockK lateinit var exchangeToPrefCurrencyMock: ExchangePriceToPreferredCurrency
    @MockK lateinit var currencyPreferenceStreamMock: GetCurrencyPreferenceStream
    @MockK lateinit var walletMapper: WalletMapper
    //endregion

    private lateinit var sut: WalletListViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = WalletListViewModel(
            activeCoinsPriceStreamMock,
            walletListStreamMock,
            refreshPortfolioValueMock,
            exchangeToPrefCurrencyMock,
            currencyPreferenceStreamMock,
            walletMapper
        )
        sut.viewStateStream.observeForever(stateObserver)
    }

    @Test
    fun `initialize pushes the wallet list load progress updates through the viewStateStream`() {
        // Arrange
        setupSuccessMocks()
        val state1 = slot<WalletListViewState>()
        val state2 = slot<WalletListViewState>()

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(capture(state1))
            stateObserver.onChanged(capture(state2))
            ignorePortfolioRefresh()
        }
        state1.captured.isLoadingWallets shouldBe true
        state2.captured.isLoadingWallets shouldBe false
    }

    @Test
    fun `initialize loads the wallet list and emits them on the viewStateStream`() {
        // Arrange
        setupSuccessMocks()
        val state = slot<WalletListViewState>()

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
            ignorePortfolioRefresh()
        }

        state.captured.wallets.size shouldBe wallets.size

        coVerify {
            wallets.forEach { wallet ->
                walletMapper.map(wallet, fakeExchangeResult)
            }
        }
    }

    @Test
    fun `initialize enables the empty view state when the loaded wallet list is empty`() {
        // Arrange
        setupSuccessMocks()
        coEvery { walletListStreamMock.invoke() } returns flowOf(emptyList())
        val state = slot<WalletListViewState>()

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
            ignorePortfolioRefresh()
        }

        state.captured.isEmpty shouldBe true
    }

    @Test
    fun `initialize disables the empty view state when the loaded wallet list isn't empty`() {
        // Arrange
        setupSuccessMocks()
        val state = slot<WalletListViewState>()

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
            ignorePortfolioRefresh()
        }

        state.captured.isEmpty shouldBe false
    }

    // TODO: Implement `prices are refreshed when the wallet list changes` test case

    // TODO: Implement test case to verify that the wallets are updated when the preferred currency changes

    @Test
    fun `viewStateStream emits the correct price refresh progress updates when refreshSelected is called`() {
        // Arrange
        val state1 = slot<WalletListViewState>()
        val state2 = slot<WalletListViewState>()
        coEvery { refreshPortfolioValueMock.invoke() } returns Right(200.0.toPrice())
        setupSuccessMocks()

        // Act
        sut.refreshSelected()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(capture(state1))
            stateObserver.onChanged(capture(state2))
        }

        state1.captured.isRefreshingPrices shouldBe true
        state2.captured.isRefreshingPrices shouldBe false
    }

    @Test
    fun `a fleeting error is emitted when the price refresh fails`() {
        // Arrange
        val captor = slot<Event<FleetingError>>()
        val observer = mockk<Observer<Event<FleetingError>>>(relaxUnitFun = true)
        sut.fleetingErrorStream.observeForever(observer)
        coEvery { refreshPortfolioValueMock.invoke(any()) } returns Failure.Unknown.toLeft()

        // Act
        sut.refreshSelected()

        // Assert
        verifySequence { observer.onChanged(capture(captor)) }
    }

    @Test
    fun `viewStateStream emits a None error when the price refresh is successful and there wasn't a previous error`() {
        // Arrange
        val state = slot<WalletListViewState>()
        setupSuccessMocks()
        coEvery { refreshPortfolioValueMock.invoke() } returns Right(200.0.toPrice())

        // Act
        sut.refreshSelected()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
        }

        state.captured.error shouldBeA Error.None::class
    }

    @Test
    fun `viewStateStream maintains the current error when the price refresh is successful`() {
        // Arrange
        val state = slot<WalletListViewState>()
        setupSuccessMocks()
        coEvery { refreshPortfolioValueMock.invoke() } returns Right(200.0.toPrice())

        // Act
        sut.viewStateStream.value = WalletListViewState(error = Error.WalletsNotLoaded)
        sut.refreshSelected()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the forced error state
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
        }

        state.captured.error shouldBeA Error.WalletsNotLoaded::class
    }

    @Test
    fun `viewStateStream emits a WalletsNotLoadedError when the coin prices stream fails`() {
        // Arrange
        val state = slot<WalletListViewState>()
        setupSuccessMocks()
        coEvery { activeCoinsPriceStreamMock.invoke() } returns flow { error("Mock") }

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
            ignorePortfolioRefresh()
        }

        state.captured.error shouldBeA Error.WalletsNotLoaded::class
    }

    @Test
    fun `viewStateStream disables the empty state when the coin prices stream fails`() {
        // Arrange
        val state = slot<WalletListViewState>()
        setupSuccessMocks()
        coEvery { activeCoinsPriceStreamMock.invoke() } returns flow { error("Mock") }

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
            ignorePortfolioRefresh()
        }

        state.captured.isEmpty shouldBe false
    }

    @Test
    fun `viewStateStream emits a WalletsNotLoadedError when the wallet list stream fails`() {
        // Arrange
        val state = slot<WalletListViewState>()
        setupSuccessMocks()
        coEvery { walletListStreamMock.invoke() } returns flow { error("Mock") }

        // Act
        sut.initialize()

        // Assert
        verifySequence {
            ignoreDefaultState()
            stateObserver.onChanged(any()) // Ignore the load progress update
            stateObserver.onChanged(capture(state))
        }

        state.captured.error shouldBeA Error.WalletsNotLoaded::class
    }

    //region Helpers
    private fun MockKVerificationScope.ignoreDefaultState() {
        stateObserver.onChanged(any())
    }

    private fun MockKVerificationScope.ignorePortfolioRefresh() {
        stateObserver.onChanged(any())
        stateObserver.onChanged(any())
    }

    private fun setupSuccessMocks() {
        coEvery { walletListStreamMock.invoke() } returns flowOf(wallets)
        coEvery { activeCoinsPriceStreamMock.invoke() } returns flowOf(prices)
        coEvery { refreshPortfolioValueMock.invoke(any()) } returns 10.0.toPrice().toRight()
        coEvery { walletMapper.map(any(), any()) } returns WalletListViewState.Wallet(
            id = 0,
            iconUrl = "",
            value = fakeWalletPrice,
            coin = WalletListViewState.Coin("", fakeWalletPrice),
            amountOfCoin = BigDecimal.ZERO
        )
        every { currencyPreferenceStreamMock.invoke() } returns flowOf(USD)
        coEvery { exchangeToPrefCurrencyMock.execute(any()) } returns fakeExchangeResult
    }

    private val wallets = listOf(
        Wallet(Bitcoin, 0.5, 1),
        Wallet(Ethereum, 1.2, 2),
        Wallet(Ripple, 50.0, 3)
    )

    private val prices = mapOf(
        Bitcoin to 12_300.0.toPrice(),
        Ethereum to 398.0.toPrice(),
        Ripple to 0.34.toPrice()
    )

    private val fakeExchangeResult = 0.0.toPrice()
    private val fakeWalletPrice = WalletListViewState.Price(BigDecimal.ZERO, USD, false)
    //endregion

}