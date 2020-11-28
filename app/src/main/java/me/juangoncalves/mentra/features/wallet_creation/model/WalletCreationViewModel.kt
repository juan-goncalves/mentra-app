package me.juangoncalves.mentra.features.wallet_creation.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.FindCoinsByName
import me.juangoncalves.mentra.domain.usecases.coin.GetCoins
import me.juangoncalves.mentra.domain.usecases.wallet.CreateWallet
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.requireRight
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.features.common.Event
import java.math.BigDecimal

class WalletCreationViewModel @ViewModelInject constructor(
    private val getCoins: GetCoins,
    private val createWallet: CreateWallet,
    private val findCoinsByName: FindCoinsByName
) : ViewModel() {

    sealed class Step {
        object CoinSelection : Step()
        object AmountInput : Step()
        object Done : Step()
    }

    sealed class Error {
        object None : Error()
        object CoinsNotLoaded : Error()
    }

    val coinListStream = MutableLiveData<List<Coin>>(emptyList())
    val isLoadingCoinListStream = MutableLiveData<Boolean>(true)
    val isSaveActionEnabledStream = MutableLiveData<Boolean>(false)
    val shouldShowSaveProgressIndicatorStream = MutableLiveData<Boolean>(false)
    val shouldShowNoMatchesWarningStream = MutableLiveData<Boolean>(false)
    val errorStream = MutableLiveData<Error>(Error.None)
    val currentStepStream = MutableLiveData<Step>(Step.CoinSelection)
    val amountInputValidationStream = MutableLiveData<Int?>(null)
    val selectedCoinStream = MutableLiveData<Coin?>(null)
    val fleetingErrorStream = MutableLiveData<Event<Int>>()

    private var amountInput: BigDecimal? = null
    private var filterJob: Job? = null

    fun initialize() {
        fetchCoins()
    }

    fun submitQuery(query: String) {
        val workInProgress = filterJob?.isActive ?: false
        if (workInProgress) filterJob?.cancel()

        filterJob = viewModelScope.launch {
            val result = findCoinsByName(query)
            result.rightValue?.let { filteredCoins ->
                coinListStream.value = filteredCoins
                shouldShowNoMatchesWarningStream.value = filteredCoins.isEmpty()
            }
        }
    }

    fun selectCoin(coin: Coin) {
        selectedCoinStream.value = coin
        currentStepStream.value = Step.AmountInput
    }

    fun backPressed() {
        val currentStep = currentStepStream.value ?: return

        currentStepStream.value = when (currentStep) {
            Step.AmountInput -> {
                amountInputValidationStream.value = null
                amountInput = null
                Step.CoinSelection
            }
            Step.CoinSelection -> Step.Done
            Step.Done -> Step.Done
        }
    }

    fun amountInputChanged(text: CharSequence?) {
        val (validationMessageId, parsedAmount) = validateAndParseAmountInput(text)
        amountInput = if (validationMessageId != null) null else parsedAmount
        amountInputValidationStream.value = validationMessageId
        isSaveActionEnabledStream.value = validationMessageId == null
    }

    fun saveSelected() {
        val selectedCoin = selectedCoinStream.value ?: return
        val amountInput = amountInput ?: return
        val wallet = Wallet(selectedCoin, amountInput)

        viewModelScope.launch {
            isSaveActionEnabledStream.value = false
            shouldShowSaveProgressIndicatorStream.value = true

            val result = createWallet(wallet)
            if (result.isLeft()) {
                isSaveActionEnabledStream.value = true
                fleetingErrorStream.value = Event(R.string.create_wallet_error)
            } else {
                currentStepStream.value = Step.Done
            }

            shouldShowSaveProgressIndicatorStream.value = false
        }
    }

    fun retryLoadCoinListSelected() {
        fetchCoins()
    }

    private fun fetchCoins() = viewModelScope.launch {
        isLoadingCoinListStream.value = true
        errorStream.value = Error.None

        val result = getCoins(Unit)

        if (result.isLeft()) {
            errorStream.value = Error.CoinsNotLoaded
        } else {
            coinListStream.value = result.requireRight()
            errorStream.value = Error.None
        }

        isLoadingCoinListStream.value = false
    }

    private fun validateAndParseAmountInput(text: CharSequence?): Pair<Int?, BigDecimal> {
        if (text.isNullOrEmpty()) return R.string.required_field to BigDecimal.ZERO

        val amount = text.toString().toBigDecimalOrNull()
            ?: return R.string.invalid_number to BigDecimal.ZERO

        return if (amount <= BigDecimal.ZERO) R.string.invalid_amount_warning to BigDecimal.ZERO else null to amount
    }

}
