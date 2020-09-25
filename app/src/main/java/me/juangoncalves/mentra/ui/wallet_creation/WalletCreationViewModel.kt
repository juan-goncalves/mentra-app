package me.juangoncalves.mentra.ui.wallet_creation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
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
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.ui.common.Event
import java.util.*

typealias WarningEvent = Event<Int>

class WalletCreationViewModel @ViewModelInject constructor(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    val coins: LiveData<List<Coin>> get() = _coins
    val shouldScrollToStart: LiveData<Boolean> get() = _shouldScrollToStart
    val warning: LiveData<WarningEvent> get() = _warning
    val onSuccessfulSave: LiveData<Unit> get() = _onSuccessfulSave

    private val _coins: MutableLiveData<List<Coin>> = MutableLiveData(emptyList())
    private val _shouldScrollToStart: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _warning: MutableLiveData<WarningEvent> = MutableLiveData()
    private val _onSuccessfulSave: MutableLiveData<Unit> = MutableLiveData()

    private var unfilteredCoins: List<Coin> = emptyList()
    private var filterJob: Job? = null

    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = coinRepository.getCoins()
            val coins: List<Coin> = result.fold(
                left = { emptyList() },
                right = { it }
            )

            _coins.postValue(coins)
            unfilteredCoins = coins
        }
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

        viewModelScope.launch(Dispatchers.IO) {
            val wallet = Wallet(coin, parsedAmount)
            val result = walletRepository.createWallet(wallet)
            if (result.isLeft()) {
                // TODO: Show error / retry
            } else {
                _onSuccessfulSave.postValue(Unit)
            }
        }
    }

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
