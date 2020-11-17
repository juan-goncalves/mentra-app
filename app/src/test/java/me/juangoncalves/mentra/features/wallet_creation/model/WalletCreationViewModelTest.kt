package me.juangoncalves.mentra.features.wallet_creation.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.MainCoroutineRule
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.errors.WalletCreationFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.FindCoinsByName
import me.juangoncalves.mentra.domain.usecases.coin.GetCoins
import me.juangoncalves.mentra.domain.usecases.wallet.CreateWallet
import me.juangoncalves.mentra.extensions.toLeft
import me.juangoncalves.mentra.features.common.DisplayError
import me.juangoncalves.mentra.features.common.Event
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationViewModel.Step
import me.juangoncalves.mentra.toRight
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WalletCreationViewModelTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    @MockK lateinit var getCoinsMock: GetCoins
    @MockK lateinit var createWalletMock: CreateWallet
    @MockK lateinit var findCoinsByNameMock: FindCoinsByName
    //endregion

    private lateinit var sut: WalletCreationViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = WalletCreationViewModel(getCoinsMock, createWalletMock, findCoinsByNameMock)
    }

    @Test
    fun `first step emitted from the currentStepStream is the coin selection`() {
        // Arrange
        val observer = mockk<Observer<Step>>(relaxUnitFun = true)
        sut.currentStepStream.observeForever(observer)

        // Assert
        verifySequence {
            observer.onChanged(Step.CoinSelection)
        }
    }

    @Test
    fun `amountInputValidationStream is initialized without a validation message code (null)`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.amountInputValidationStream.observeForever(observer)

        // Assert
        verifySequence {
            observer.onChanged(null)
        }
    }

    @Test
    fun `coin selection step is emitted when the back button is pressed in the amount input step`() {
        // Arrange
        val observer = mockk<Observer<Step>>(relaxUnitFun = true)
        sut.currentStepStream.value = Step.AmountInput
        sut.currentStepStream.observeForever(observer)

        // Act
        sut.backPressed()

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(Step.CoinSelection)
        }
    }

    @Test
    fun `done step is emitted when the back button is pressed in the coin selection step`() {
        // Arrange
        val observer = mockk<Observer<Step>>(relaxUnitFun = true)
        sut.currentStepStream.value = Step.CoinSelection
        sut.currentStepStream.observeForever(observer)

        // Act
        sut.backPressed()

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(Step.Done)
        }
    }

    @Test
    fun `amount input validations are reset when navigating to the previous step`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.currentStepStream.value = Step.AmountInput
        sut.amountInputValidationStream.value = 1
        sut.amountInputValidationStream.observeForever(observer)

        // Act
        sut.backPressed()

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(null)
        }
    }

    @Test
    fun `amount input step is emitted when a coin is selected`() {
        // Arrange
        val observer = mockk<Observer<Step>>(relaxUnitFun = true)
        sut.currentStepStream.observeForever(observer)

        // Act
        sut.selectCoin(Bitcoin)

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(Step.AmountInput)
        }
    }

    @Test
    fun `selectedCoinStream is updated when a coin is selected`() {
        // Arrange
        val observer = mockk<Observer<Coin?>>(relaxUnitFun = true)
        sut.selectedCoinStream.observeForever(observer)

        // Act
        sut.selectCoin(Bitcoin)

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(Bitcoin)
        }
    }

    @Test
    fun `shouldShowNoMatchesWarningStream emits TRUE when there are no results on a query`() {
        // Arrange
        val observer = mockk<Observer<Boolean>>(relaxUnitFun = true)
        sut.shouldShowNoMatchesWarningStream.observeForever(observer)
        coEvery { findCoinsByNameMock.invoke(any()) } returns emptyList<Coin>().toRight()

        // Act
        sut.submitQuery("no matches query")

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(true)
        }
    }

    @Test
    fun `shouldShowNoMatchesWarningStream emits FALSE when there are results on a query`() {
        // Arrange
        val observer = mockk<Observer<Boolean>>(relaxUnitFun = true)
        sut.shouldShowNoMatchesWarningStream.observeForever(observer)
        coEvery { findCoinsByNameMock.invoke(any()) } returns listOf(Bitcoin).toRight()

        // Act
        sut.submitQuery("matching")

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(false)
        }
    }

    @Test
    fun `amountInputValidationStream emits a invalid_number when the input is not a number`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.amountInputValidationStream.observeForever(observer)

        // Act
        sut.amountInputChanged("hello")

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(R.string.invalid_number)
        }
    }

    @Test
    fun `amountInputValidationStream emits a required_field message when the input is null`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.amountInputValidationStream.observeForever(observer)

        // Act
        sut.amountInputChanged(null)

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(R.string.required_field)
        }
    }

    @Test
    fun `amountInputValidationStream emits a required_field when the input is empty`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.amountInputValidationStream.observeForever(observer)

        // Act
        sut.amountInputChanged("")

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(R.string.required_field)
        }
    }

    @Test
    fun `amountInputValidationStream emits a invalid_amount_warning when the input is 0`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.amountInputValidationStream.observeForever(observer)

        // Act
        sut.amountInputChanged("0.00")

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(R.string.invalid_amount_warning)
        }
    }

    @Test
    fun `amountInputValidationStream emits a invalid_amount_warning when the input is less than 0`() {
        // Arrange
        val observer = mockk<Observer<Int?>>(relaxUnitFun = true)
        sut.amountInputValidationStream.observeForever(observer)

        // Act
        sut.amountInputChanged("-30.02")

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(R.string.invalid_amount_warning)
        }
    }

    @Test
    fun `a wallet with the input attributes is created when the save action is selected`() {
        // Arrange
        val captor = slot<Wallet>()
        coEvery { createWalletMock.invoke(any()) } returns Unit.toRight()

        // Act
        sut.selectCoin(Bitcoin)
        sut.amountInputChanged("1.0")
        sut.saveSelected()

        // Assert
        coVerify { createWalletMock.invoke(capture(captor)) }
        assertEquals(Bitcoin, captor.captured.coin)
        assertThat(captor.captured.amount, closeTo(1.0, 0.0001))
    }

    @Test
    fun `a fleeting error is emitted when the wallet creation fails`() {
        // Arrange
        val observer = mockk<Observer<Event<DisplayError>>>(relaxUnitFun = true)
        sut.fleetingErrorStream.observeForever(observer)
        coEvery { createWalletMock.invoke(any()) } returns WalletCreationFailure().toLeft()

        // Act
        sut.selectCoin(Bitcoin)
        sut.amountInputChanged("1.0")
        sut.saveSelected()

        // Assert
        verifySequence {
            observer.onChanged(any())
        }
    }

    @Test
    fun `done step is emitted after successfully creating a wallet`() {
        // Arrange
        val observer = mockk<Observer<Step>>(relaxUnitFun = true)
        sut.currentStepStream.observeForever(observer)
        coEvery { createWalletMock.invoke(any()) } returns Unit.toRight()

        // Act
        sut.selectCoin(Bitcoin)
        sut.amountInputChanged("1.0")
        sut.saveSelected()

        // Assert
        verifySequence {
            observer.onChanged(any()) // Ignore the current value received when subscribed
            observer.onChanged(any()) // Ignore the coin selection
            observer.onChanged(Step.Done)
        }
    }

    // TODO: test coin load, filtering and error state

    //region Helpers
    //endregion

}