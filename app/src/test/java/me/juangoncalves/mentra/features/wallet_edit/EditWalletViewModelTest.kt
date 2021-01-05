package me.juangoncalves.mentra.features.wallet_edit

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.USD
import me.juangoncalves.mentra.common.BundleKeys
import me.juangoncalves.mentra.common.Event
import me.juangoncalves.mentra.common.Notification
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.usecases.wallet.UpdateWallet
import me.juangoncalves.mentra.failures.FleetingError
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import me.juangoncalves.mentra.test_utils.MainCoroutineRule
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class EditWalletViewModelTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    @MockK lateinit var updateWalletMock: UpdateWallet
    @MockK lateinit var estimateObserver: Observer<Price>
    @MockK lateinit var inputWarningObserver: Observer<Int?>
    @MockK lateinit var saveButtonStateObserver: Observer<Boolean>
    @MockK lateinit var dismissObserver: Observer<Notification>
    @MockK lateinit var fleetingErrorObserver: Observer<Event<FleetingError>>
    //endregion

    private lateinit var sut: EditWalletViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = EditWalletViewModel(updateWalletMock)
        sut.estimatedValueStream.observeForever(estimateObserver)
        sut.amountInputValidationStream.observeForever(inputWarningObserver)
        sut.saveButtonStateStream.observeForever(saveButtonStateObserver)
        sut.dismissStream.observeForever(dismissObserver)
        sut.fleetingErrorStream.observeForever(fleetingErrorObserver)
    }

    @Test(expected = IllegalStateException::class)
    fun `initialize without wallet throws a IllegalStateException`() {
        // Arrange
        val args = bundleOf()

        // Act
        sut.initialize(args)

        // Assert
    }

    @Test(expected = IllegalStateException::class)
    fun `initialize without arguments throws a IllegalStateException`() {
        // Arrange
        // Act
        sut.initialize(null)

        // Assert
    }

    @Test
    fun `estimatedValueStream emits the wallet value after initialization`() {
        // Arrange
        val args = bundleOf(BundleKeys.Wallet to fakeWallet)
        val captor = slot<Price>()

        // Act
        sut.initialize(args)

        // Assert
        verify(exactly = 1) { estimateObserver.onChanged(capture(captor)) }
        captor.captured.value shouldBeCloseTo fakeWallet.value.value.toDouble()
    }

    @Test
    fun `displayWallet attribute matches the one received at initialization`() {
        // Arrange
        val args = bundleOf(BundleKeys.Wallet to fakeWallet)

        // Act
        sut.initialize(args)

        // Assert
        fakeWallet shouldBe sut.wallet
    }

    @Test
    fun `estimatedValueStream emits the correct estimation when the amount input changes`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = slot<Price>()

        // Act
        sut.amountInputChanged("1.0")

        // Assert
        verify(exactly = 2) { estimateObserver.onChanged(capture(captor)) }
        captor.captured.value shouldBeCloseTo 11384.23
    }

    @Test
    fun `estimatedValueStream emits 0 when the input amount is invalid`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = slot<Price>()

        // Act
        sut.amountInputChanged("invalid")

        // Assert
        verify(exactly = 2) { estimateObserver.onChanged(capture(captor)) }
        captor.captured.value shouldBeCloseTo 0.0
    }

    @Test
    fun `amountInputValidationStream emits the required field message when the input is empty`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Int?>()

        // Act
        sut.amountInputChanged("")

        // Assert
        verify(exactly = 2) { inputWarningObserver.onChanged(captureNullable(captor)) }
        captor.last() shouldBe R.string.required_field
    }

    @Test
    fun `amountInputValidationStream emits the invalid amount message when the input is negative`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Int?>()

        // Act
        sut.amountInputChanged("-10.0")

        // Assert
        verify(exactly = 2) { inputWarningObserver.onChanged(captureNullable(captor)) }
        captor.last() shouldBe R.string.invalid_amount_warning
    }

    @Test
    fun `amountInputValidationStream emits the invalid number message when the input is not parsable`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Int?>()

        // Act
        sut.amountInputChanged("1sj2.23s")

        // Assert
        verify(exactly = 2) { inputWarningObserver.onChanged(captureNullable(captor)) }
        captor.last() shouldBe R.string.invalid_number
    }

    @Test
    fun `amountInputValidationStream emits null when the input is valid`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Int?>()

        // Act
        sut.amountInputChanged("1222223.23121")

        // Assert
        verify(exactly = 2) {
            inputWarningObserver.onChanged(any()) // Validation right after initialization
            inputWarningObserver.onChanged(captureNullable(captor))
        }

        captor.last() shouldBe null
    }

    @Test
    fun `saveButtonStateStream emits TRUE when the amount input is valid`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Boolean>()

        // Act
        sut.amountInputChanged("82321.1231")

        // Assert
        verify(exactly = 2) { saveButtonStateObserver.onChanged(capture(captor)) }
        captor.last() shouldBe true
    }

    @Test
    fun `saveButtonStateStream emits FALSE when the amount input is invalid`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Boolean>()

        // Act
        sut.amountInputChanged("wrong")

        // Assert
        verify(exactly = 2) { saveButtonStateObserver.onChanged(capture(captor)) }
        captor.last() shouldBe false
    }

    @Test
    fun `saveButtonStateStream emits FALSE when the amount has not been modified`() {
        // Arrange
        initSutWithFakeWallet()
        val captor = mutableListOf<Boolean>()

        // Act
        sut.amountInputChanged(fakeWallet.amountOfCoin.toString())

        // Assert
        verify(exactly = 2) { saveButtonStateObserver.onChanged(capture(captor)) }
        captor.last() shouldBe false
    }

    @Test
    fun `dismissStream emits a Notification when the cancel option is selected`() {
        // Arrange
        initSutWithFakeWallet()

        // Act
        sut.cancelSelected()

        // Assert
        verify(exactly = 1) { dismissObserver.onChanged(any()) }
    }

    @Test
    fun `saveSelected passes the amount input value to the update use case`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { updateWalletMock.invoke(any()) } returns Right(Unit)
        val captor = slot<UpdateWallet.Params>()

        // Act
        sut.amountInputChanged("3.456")
        sut.saveSelected()

        // Assert
        coVerify { updateWalletMock.invoke(capture(captor)) }
        captor.captured.newAmount shouldBeCloseTo 3.456
    }

    @Test
    fun `savedUpdates is TRUE after a successful wallet update`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { updateWalletMock.invoke(any()) } returns Right(Unit)

        // Act
        sut.saveSelected()

        // Assert
        sut.savedUpdates shouldBe true
    }

    @Test
    fun `dismissStream emits a Notification after a successful wallet update`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { updateWalletMock.invoke(any()) } returns Right(Unit)

        // Act
        sut.saveSelected()

        // Assert
        verify(exactly = 1) { dismissObserver.onChanged(any()) }
    }

    @Test
    fun `fleetingErrorStream emits an error when the wallet update fails`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { updateWalletMock.invoke(any()) } returns Failure.Unknown.toLeft()

        // Act
        sut.saveSelected()

        // Assert
        verify(exactly = 1) { fleetingErrorObserver.onChanged(any()) }
    }

    //region Helpers
    private val fakeWallet = WalletListViewState.Wallet(
        id = 1,
        iconUrl = "https://someurl.com/img.png",
        value = WalletListViewState.Price((0.2312 * 11384.23).toBigDecimal(), USD, false),
        amountOfCoin = 0.2312.toBigDecimal(),
        coin = WalletListViewState.Coin(
            Bitcoin.name,
            WalletListViewState.Price(11384.23.toBigDecimal(), USD, false)
        )
    )

    private fun initSutWithFakeWallet() {
        val args = bundleOf(BundleKeys.Wallet to fakeWallet)
        sut.initialize(args)
    }
    //endregion

}