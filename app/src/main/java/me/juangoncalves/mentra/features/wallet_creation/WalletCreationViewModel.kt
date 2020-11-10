package me.juangoncalves.mentra.features.wallet_creation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.GetCoins
import me.juangoncalves.mentra.domain.usecases.wallet.CreateWallet
import me.juangoncalves.mentra.features.common.FleetingErrorPublisher
import me.juangoncalves.mentra.features.common.FleetingErrorPublisherImpl
import me.juangoncalves.mentra.features.common.executor
import me.juangoncalves.mentra.features.wallet_creation.models.WalletCreationState
import me.juangoncalves.mentra.features.wallet_creation.models.WalletCreationState.Error
import me.juangoncalves.mentra.features.wallet_creation.models.WalletCreationState.Step
import java.util.*

class WalletCreationViewModel @ViewModelInject constructor(
    private val _getCoins: GetCoins,
    private val _createWallet: CreateWallet
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val viewStateStream = MutableLiveData<WalletCreationState>(WalletCreationState())

    private val currentViewState: WalletCreationState get() = viewStateStream.value!!
    private var unfilteredCoins: List<Coin> = emptyList()
    private var filterJob: Job? = null

    init {
        fetchCoins()
    }

    private fun fetchCoins() = viewModelScope.launch {
        viewStateStream.value = currentViewState.copy(isLoadingCoins = true)

        val result = _getCoins(Unit)

        viewStateStream.value = result.fold(
            left = {
                currentViewState.copy(
                    error = Error.CoinsNotLoaded,
                    isLoadingCoins = false
                )
            },
            right = { coins ->
                unfilteredCoins = coins
                currentViewState.copy(
                    error = Error.None,
                    isLoadingCoins = false,
                    coins = coins
                )
            }
        )
    }

    fun submitQuery(query: String) {
        if (query.length in 1..2) return

        if (query.isEmpty()) {
            viewStateStream.value = currentViewState.copy(coins = unfilteredCoins)
            return
        }

        val workInProgress = filterJob?.isActive ?: false
        if (workInProgress) filterJob?.cancel()

        filterJob = viewModelScope.launch {
            viewStateStream.value = currentViewState.copy(coins = filterCoinsByName(query))
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

        _createWallet.executor()
            .inScope(viewModelScope)
            .onSuccess {
                viewStateStream.value = currentState.copy(currentStep = Step.Done)
            }
            .onFailurePublishFleetingError()
            .run(wallet)
    }

    // TODO: Move into use case that receives the default dispatcher by DI?
    private suspend fun filterCoinsByName(query: String): List<Coin> =
        withContext(Dispatchers.Default) {
            return@withContext unfilteredCoins.filter { coin ->
                val comparableCoinName = coin.name.toLowerCase(Locale.ROOT)
                comparableCoinName.contains(query.toLowerCase(Locale.ROOT))
            }.sortedBy { match ->
                match.name.length - query.length
            }
        }

    private fun validateAndParseAmountInput(text: CharSequence?): Pair<Int?, Double> {
        if (text.isNullOrEmpty()) return R.string.required_field to 0.0

        val amount = text.toString().toDoubleOrNull() ?: return R.string.invalid_number to 0.0

        return if (amount <= 0) R.string.invalid_amount_warning to 0.0 else null to amount
    }

}
