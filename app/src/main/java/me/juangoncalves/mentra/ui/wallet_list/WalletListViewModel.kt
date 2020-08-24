package me.juangoncalves.mentra.ui.wallet_list

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
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.GetCoinPriceUseCase
import me.juangoncalves.mentra.domain.usecases.GetGradientCoinIconUseCase
import me.juangoncalves.mentra.domain.usecases.GetWalletsUseCase
import me.juangoncalves.mentra.ui.common.DisplayError

class WalletListViewModel @ViewModelInject constructor(
    private val getWallets: GetWalletsUseCase,
    private val getCoinPrice: GetCoinPriceUseCase,
    private val getGradientCoinIcon: GetGradientCoinIconUseCase
) : ViewModel() {

    val shouldShowProgressBar: LiveData<Boolean> get() = _shouldShowProgressBar
    val wallets: LiveData<List<DisplayWallet>> get() = _wallets
    val error: LiveData<DisplayError> get() = _error

    private val _shouldShowProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _wallets: MutableLiveData<List<DisplayWallet>> = MutableLiveData(emptyList())
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()

    init {
        refreshWallets()
    }

    private fun refreshWallets() {
        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowProgressBar.postValue(true)

            val getWalletsResult = getWallets()
            if (getWalletsResult is Failure) {
                val error = DisplayError(R.string.default_error, ::refreshWallets)
                _error.postValue(error)
                _shouldShowProgressBar.postValue(false)
                return@launch
            }

            val wallets = getWalletsResult.fold(
                left = { emptyList<Wallet>() },
                right = { it }
            )

            _wallets.postValue(placeholdersFor(wallets))

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

            val displayWallets = wallets.mapNotNull { wallet ->
                coinPrices[wallet.coin]?.let { price ->
                    DisplayWallet(
                        wallet,
                        getGradientCoinIcon(wallet.coin),
                        price,
                        price * wallet.amount,
                        emptyList()
                    )
                }
            }

            _wallets.postValue(displayWallets)
            _shouldShowProgressBar.postValue(false)
        }
    }

    private fun placeholdersFor(wallets: List<Wallet>): List<DisplayWallet> {
        return wallets.map { wallet ->
            DisplayWallet(wallet, getGradientCoinIcon(wallet.coin), -1.0, -1.0, emptyList())
        }
    }

}
