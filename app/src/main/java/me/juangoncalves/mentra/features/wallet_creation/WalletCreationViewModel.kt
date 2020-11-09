package me.juangoncalves.mentra.features.wallet_creation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.GetCoins
import me.juangoncalves.mentra.domain.usecases.wallet.CreateWallet
import me.juangoncalves.mentra.features.common.*
import java.util.*

typealias WarningEvent = Event<Int>

class WalletCreationViewModel @ViewModelInject constructor(
    private val _getCoins: GetCoins,
    private val _createWallet: CreateWallet
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val coins: LiveData<List<Coin>> get() = _coins
    val shouldScrollToStart: LiveData<Boolean> get() = _shouldScrollToStart
    val warning: LiveData<WarningEvent> get() = _warning
    val onSuccessfulSave: LiveData<Unit> get() = _onSuccessfulSave
    val shouldShowCoinLoadIndicator: LiveData<Boolean> get() = _shouldShowCoinLoadIndicator

    private val _coins: MutableLiveData<List<Coin>> = MutableLiveData(emptyList())
    private val _shouldScrollToStart: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _warning: MutableLiveData<WarningEvent> = MutableLiveData()
    private val _onSuccessfulSave: MutableLiveData<Unit> = MutableLiveData()
    private val _shouldShowCoinLoadIndicator: MutableLiveData<Boolean> = MutableLiveData(false)

    private var unfilteredCoins: List<Coin> = emptyList()
    private var filterJob: Job? = null

    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        _getCoins.executor()
            .inScope(viewModelScope)
            .beforeInvoke { _shouldShowCoinLoadIndicator.postValue(true) }
            .afterInvoke { _shouldShowCoinLoadIndicator.postValue(false) }
            .onSuccess { coins ->
                _coins.postValue(coins)
                unfilteredCoins = coins
            }
            .onFailurePublishFleetingError()
            .run()
    }

    fun submitQuery(query: String) {
        if (query.length in 1..2) return

        if (query.isEmpty()) {
            _coins.postValue(unfilteredCoins)
            return
        }

        val workInProgress = filterJob?.isActive ?: false
        if (workInProgress) filterJob?.cancel()

        filterJob = viewModelScope.launch {
            val matching = filterCoinsByName(query)
            _coins.value = matching
            _shouldScrollToStart.value = true
        }
    }

    fun submitForm(coin: Coin?, amount: String) {
        if (coin == null) {
            _warning.postValue(WarningEvent(R.string.no_coin_selected_warning))
            return
        }

        val parsedAmount = amount.toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0) {
            _warning.postValue(WarningEvent(R.string.invalid_amount_warning))
            return
        }

        val wallet = Wallet(coin, parsedAmount)

        _createWallet.executor()
            .inScope(viewModelScope)
            .onSuccess { _onSuccessfulSave.postValue(Unit) }
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

}