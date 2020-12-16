package me.juangoncalves.mentra.features.wallet_deletion

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.USD
import me.juangoncalves.mentra.domain_layer.errors.StorageFailure
import me.juangoncalves.mentra.domain_layer.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.features.common.BundleKeys
import me.juangoncalves.mentra.features.common.DisplayError
import me.juangoncalves.mentra.features.common.Event
import me.juangoncalves.mentra.features.common.Notification
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import me.juangoncalves.mentra.test_utils.MainCoroutineRule
import me.juangoncalves.mentra.test_utils.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class DeleteWalletViewModelTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    @MockK lateinit var deleteWalletMock: DeleteWallet
    @MockK lateinit var dismissObserver: Observer<Notification>
    @MockK lateinit var fleetingErrorObserver: Observer<Event<DisplayError>>
    //endregion

    private lateinit var sut: DeleteWalletViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = DeleteWalletViewModel(deleteWalletMock)
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
    fun `displayWallet attribute matches the one received at initialization`() {
        // Arrange
        val args = bundleOf(BundleKeys.Wallet to fakeWallet)

        // Act
        sut.initialize(args)

        // Assert
        fakeWallet shouldBe sut.wallet
    }

    @Test
    fun `dismissStream emits a notification after a successful wallet deletion`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { deleteWalletMock.invoke(any()) } returns Right(Unit)

        // Act
        sut.deleteSelected()

        // Assert
        verify(exactly = 1) { dismissObserver.onChanged(any()) }
    }

    @Test
    fun `fleetingErrorStream emits an error when the wallet deletion fails`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { deleteWalletMock.invoke(any()) } returns Left(StorageFailure())

        // Act
        sut.deleteSelected()

        // Assert
        verify(exactly = 1) { fleetingErrorObserver.onChanged(any()) }
    }

    @Test
    fun `walletWasDeleted is TRUE when the wallet is successfully deleted`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { deleteWalletMock.invoke(any()) } returns Right(Unit)

        // Act
        sut.deleteSelected()

        // Assert
        sut.walletWasDeleted shouldBe true
    }

    @Test
    fun `walletWasDeleted is FALSE when the wallet deletion fails`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { deleteWalletMock.invoke(any()) } returns Left(StorageFailure())

        // Act
        sut.deleteSelected()

        // Assert
        sut.walletWasDeleted shouldBe false
    }

    @Test
    fun `dismissStream emits a notification when the cancel option is selected`() {
        // Arrange
        initSutWithFakeWallet()

        // Act
        sut.cancelSelected()

        // Assert
        verify(exactly = 1) { dismissObserver.onChanged(any()) }
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