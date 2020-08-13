package me.juangoncalves.mentra.ui.wallet_list

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.FetchPriceFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.GetCoinPriceUseCase
import me.juangoncalves.mentra.domain.usecases.GetWalletsUseCase

class WalletListViewModel @ViewModelInject constructor(
    private val getWallets: GetWalletsUseCase,
    private val getCoinPrice: GetCoinPriceUseCase
) : ViewModel() {

    val viewState: LiveData<State> get() = _viewState

    private val _viewState: MutableLiveData<State> = MutableLiveData(State.Loading(false))

    init {
        refreshWallets()
    }

    fun retryWalletFetch() {
        refreshWallets()
    }

    private fun refreshWallets() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.postValue(State.Loading())

            val getWalletsResult = getWallets()
            if (getWalletsResult is Failure) {
                _viewState.postValue(failureToErrorState(getWalletsResult))
                return@launch
            }

            val wallets = getWalletsResult.fold(
                left = { emptyList<Wallet>() },
                right = { it }
            )

            _viewState.postValue(State.Loaded(placeholdersFor(wallets)))

            val coins = wallets.map { it.coin }
            val uniqueCoins = HashSet<Coin>(coins)
            val coinPrices = hashMapOf<Coin, Double>()
            uniqueCoins.forEach { coin ->
                val priceResult = getCoinPrice(coin)
                val price = priceResult.fold(
                    left = { failure ->
                        if (failure is FetchPriceFailure) {
                            // TODO: Show warning message in corresponding wallets
                            failure.storedPrice?.value ?: -1.0
                        } else {
                            // TODO: Show error message in corresponding wallets
                            -1.0
                        }
                    },
                    right = { it.value }
                )
                coinPrices[coin] = price
            }

            val displayWallets = wallets.map { wallet ->
                coinPrices[wallet.coin]?.let { price ->
                    DisplayWallet(wallet, price, price * wallet.amount, emptyList())
                }
            }

            _viewState.postValue(State.Loaded(displayWallets.filterNotNull()))
        }
    }

    private fun placeholdersFor(wallets: List<Wallet>): List<DisplayWallet> {
        return wallets.map { wallet ->
            DisplayWallet(wallet, -1.0, -1.0, emptyList())
        }
    }

    private fun failureToErrorState(failure: Failure): State.Error {
        return when (failure) {
            // TODO: Handle failure types
            else -> State.Error(R.string.default_error)
        }
    }

    sealed class State {
        class Loading(val hasLoadedData: Boolean = false) : State()

        class Error(@StringRes val messageId: Int) : State()

        class Loaded(val wallets: List<DisplayWallet>) : State()
    }

}

data class DisplayWallet(
    val wallet: Wallet,
    val currentCoinPrice: Double,
    val currentWalletPrice: Double,
    val historicPrice: List<Price>

    // In this class we could add an attribute to show a warning, for example if the price fetching
    // fails completely we can show an error indicator over the coin image, and if we didn't manage
    // to get the latest price but we had one cached, we can show a warning over the coin image
    // (Maybe a red border over the coin image for errors and a yellow one for warnings)
)
