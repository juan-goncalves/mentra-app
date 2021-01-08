package me.juangoncalves.mentra.features.wallet_creation.model

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.usecases.coin.FindCoinsByName
import me.juangoncalves.mentra.domain_layer.usecases.coin.GetCoins
import me.juangoncalves.mentra.domain_layer.usecases.wallet.CreateWallet
import me.juangoncalves.mentra.failures.FailurePublisher
import me.juangoncalves.mentra.failures.GeneralFailurePublisher
import java.math.BigDecimal

class WalletCreationViewModel @ViewModelInject constructor(
    private val getCoins: GetCoins,
    private val createWallet: CreateWallet,
    private val findCoinsByName: FindCoinsByName
) : ViewModel(), FailurePublisher by GeneralFailurePublisher() {

    val coinListStream = MutableLiveData<List<Coin>>(emptyList())
    val isLoadingCoinListStream = MutableLiveData<Boolean>(true)
    val isSaveActionEnabledStream = MutableLiveData<Boolean>(false)
    val shouldShowSaveProgressIndicatorStream = MutableLiveData<Boolean>(false)
    val shouldShowNoMatchesWarningStream = MutableLiveData<Boolean>(false)
    val errorStateStream = MutableLiveData<Error>(Error.None)
    val currentStepStream = MutableLiveData<Step>(Step.CoinSelection)
    val amountInputValidationStream = MutableLiveData<Validation>(Validation.None)
    val selectedCoinStream = MutableLiveData<Coin?>(null)

    private var amountInput: BigDecimal? = null
    private var filterJob: Job? = null

    fun initialize() {
        fetchCoins()
    }

    fun submitQuery(query: String) {
        filterJob?.cancel()
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
                amountInputValidationStream.postValue(Validation.None)
                amountInput = null
                Step.CoinSelection
            }
            Step.CoinSelection -> Step.Done
            Step.Done -> Step.Done
        }
    }

    fun amountInputChanged(text: CharSequence?) {
        val (validation, parsedAmount) = validateAndParseAmountInput(text)
        amountInput = if (validation != Validation.None) null else parsedAmount
        amountInputValidationStream.value = validation
        isSaveActionEnabledStream.value = validation == Validation.None
    }

    fun saveSelected() {
        val selectedCoin = selectedCoinStream.value ?: return
        val amountInput = amountInput ?: return
        val wallet = Wallet(selectedCoin, amountInput)

        viewModelScope.launch {
            isSaveActionEnabledStream.value = false
            shouldShowSaveProgressIndicatorStream.value = true

            createWallet.runHandlingFailure(
                params = wallet,
                onFailure = { isSaveActionEnabledStream.value = true },
                onSuccess = { currentStepStream.value = Step.Done }
            )

            shouldShowSaveProgressIndicatorStream.value = false
        }
    }

    fun retryLoadCoinListSelected() {
        fetchCoins()
    }

    private fun fetchCoins() = viewModelScope.launch {
        isLoadingCoinListStream.value = true
        errorStateStream.value = Error.None

        val result = getCoins()
        if (result.isLeft()) {
            errorStateStream.value = Error.CoinsNotLoaded
        } else {
            val coins = result.requireRight()
            coinListStream.value = coins
            errorStateStream.value = Error.None
        }

        isLoadingCoinListStream.value = false
    }

    private fun validateAndParseAmountInput(text: CharSequence?): Pair<Validation, BigDecimal> {
        if (text.isNullOrEmpty()) return Validation(R.string.required_field) to BigDecimal.ZERO

        val amount = text.toString().toBigDecimalOrNull()
            ?: return Validation(R.string.invalid_number) to BigDecimal.ZERO

        return when {
            amount <= BigDecimal.ZERO -> Validation(R.string.invalid_amount_warning) to BigDecimal.ZERO
            else -> Validation.None to amount
        }
    }

    sealed class Step {
        object CoinSelection : Step()
        object AmountInput : Step()
        object Done : Step()
    }

    sealed class Error {
        object None : Error()
        object CoinsNotLoaded : Error()
    }

    data class Validation(@StringRes val messageId: Int) {
        companion object {
            val None = Validation(0)
        }
    }

}
