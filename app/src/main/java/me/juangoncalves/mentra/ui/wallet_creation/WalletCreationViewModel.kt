package me.juangoncalves.mentra.ui.wallet_creation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.Either
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.CreateWalletUseCase
import me.juangoncalves.mentra.domain.usecases.GetCoinsUseCase
import java.util.*

class WalletCreationViewModel @ViewModelInject constructor(
    private val createWallet: CreateWalletUseCase,
    private val getCoins: GetCoinsUseCase
) : ViewModel() {

    val coins: LiveData<List<Coin>> get() = _coins
    val shouldScrollToStart: LiveData<Boolean> get() = _shouldScrollToStart

    private val _coins: MutableLiveData<List<Coin>> = MutableLiveData(emptyList())
    private val _shouldScrollToStart: MutableLiveData<Boolean> = MutableLiveData(false)

    private var unfilteredCoins: List<Coin> = emptyList()
    private var filterJob: Job? = null

    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getCoins()
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

    fun submitForm(coin: Coin, amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val wallet = Wallet(coin, amount)
            val result = createWallet(wallet)
            if (result is Either.Left) {
                // TODO: Show error / retry
            } else {
                // TODO: push navigation event / finish activity
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
