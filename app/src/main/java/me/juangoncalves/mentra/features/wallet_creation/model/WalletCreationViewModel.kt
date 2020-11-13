package me.juangoncalves.mentra.features.wallet_creation.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.FindCoinsByName
import me.juangoncalves.mentra.domain.usecases.coin.GetCoins
import me.juangoncalves.mentra.domain.usecases.wallet.CreateWallet
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationState.Error
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationState.Step

class WalletCreationViewModel @ViewModelInject constructor(
    private val getCoins: GetCoins,
    private val createWallet: CreateWallet,
    private val findCoinsByName: FindCoinsByName
) : ViewModel() {

    val viewStateStream = MutableLiveData<WalletCreationState>(WalletCreationState())

    private val currentViewState: WalletCreationState get() = viewStateStream.value!!
    private var filterJob: Job? = null

    init {
        fetchCoins()
    }

    private fun fetchCoins() = viewModelScope.launch {
        viewStateStream.value = currentViewState.copy(
            isLoadingCoins = true,
            error = Error.None
        )

        val result = getCoins(Unit)

        viewStateStream.value = result.fold(
            left = {
                currentViewState.copy(
                    error = Error.CoinsNotLoaded,
                    isLoadingCoins = false
                )
            },
            right = { coins ->
                currentViewState.copy(
                    error = Error.None,
                    isLoadingCoins = false,
                    coins = coins
                )
            }
        )
    }

    fun submitQuery(query: String) {
        val workInProgress = filterJob?.isActive ?: false
        if (workInProgress) filterJob?.cancel()

        filterJob = viewModelScope.launch {
            val result = findCoinsByName(query)
            result.rightValue?.let { filteredCoins ->
                viewStateStream.value = currentViewState.copy(coins = filteredCoins)
            }
        }
    }

    fun selectCoin(coin: Coin) {
        viewStateStream.value = currentViewState.copy(
            selectedCoin = coin,
            currentStep = Step.AmountInput
        )
    }

    fun backPressed() {
        val previousStep = when (currentViewState.currentStep) {
            Step.AmountInput -> Step.CoinSelection
            Step.CoinSelection -> Step.Done
            Step.Done -> Step.Done
        }
        viewStateStream.value = currentViewState.copy(currentStep = previousStep)
    }

    fun amountInputChanged(text: CharSequence?) {
        val (validationMessageId, parsedAmount) = validateAndParseAmountInput(text)

        viewStateStream.value = currentViewState.copy(
            amountInput = if (validationMessageId != null) null else parsedAmount,
            inputValidation = validationMessageId,
            isSaveEnabled = validationMessageId == null
        )
    }

    fun saveSelected() {
        val currentState = currentViewState
        currentState.selectedCoin ?: return
        currentState.amountInput ?: return

        val wallet = Wallet(currentState.selectedCoin, currentState.amountInput)

        viewModelScope.launch {
            val result = createWallet(wallet)
            viewStateStream.value = result.fold(
                left = { currentViewState.copy(error = Error.WalletNotCreated()) },
                right = { currentState.copy(currentStep = Step.Done, error = Error.None) }
            )
        }
    }

    fun retryLoadCoinListSelected() {
        fetchCoins()
    }

    private fun validateAndParseAmountInput(text: CharSequence?): Pair<Int?, Double> {
        if (text.isNullOrEmpty()) return R.string.required_field to 0.0

        val amount = text.toString().toDoubleOrNull() ?: return R.string.invalid_number to 0.0

        return if (amount <= 0) R.string.invalid_amount_warning to 0.0 else null to amount
    }

}
