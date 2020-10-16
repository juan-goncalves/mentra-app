package me.juangoncalves.mentra.ui.wallet_deletion

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.ui.common.BundleKeys
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Event
import me.juangoncalves.mentra.ui.common.Notification
import me.juangoncalves.mentra.ui.wallet_list.DisplayWallet
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

    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @MockK lateinit var deleteWalletMock: DeleteWallet
    @MockK lateinit var dismissObserver: Observer<Notification>
    @MockK lateinit var fleetingErrorObserver: Observer<Event<DisplayError>>

    private lateinit var sut: DeleteWalletViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = DeleteWalletViewModel(deleteWalletMock, mainCoroutineRule.dispatcher)
        sut.dismissStream.observeForever(dismissObserver)
        sut.fleetingErrorStream.observeForever(fleetingErrorObserver)
    }

    @Test(expected = IllegalStateException::class)
    fun `initialize without wallet throws a IllegalStateException`() {
        // Arrange
        val args = bundleOf()

        // Act
        sut.initialize(args)
    }

    @Test(expected = IllegalStateException::class)
    fun `initialize without arguments throws a IllegalStateException`() {
        // Act
        sut.initialize(null)
    }

    @Test
    fun `displayWallet attribute matches the one received at initialization`() {
        // Arrange
        val args = bundleOf(BundleKeys.Wallet to fakeWallet)

        // Act
        sut.initialize(args)

        // Assert
        fakeWallet equals sut.displayWallet
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
        sut.walletWasDeleted equals true
    }

    @Test
    fun `walletWasDeleted is FALSE when the wallet deletion fails`() {
        // Arrange
        initSutWithFakeWallet()
        coEvery { deleteWalletMock.invoke(any()) } returns Left(StorageFailure())

        // Act
        sut.deleteSelected()

        // Assert
        sut.walletWasDeleted equals false
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

    private val fakeWallet = DisplayWallet(
        Wallet(Bitcoin, 0.2312, 1),
        "https://someurl.com/img.png",
        11384.23,
        0.2312 * 11384.23
    )

    private fun initSutWithFakeWallet() {
        val args = bundleOf(BundleKeys.Wallet to fakeWallet)
        sut.initialize(args)
    }

}