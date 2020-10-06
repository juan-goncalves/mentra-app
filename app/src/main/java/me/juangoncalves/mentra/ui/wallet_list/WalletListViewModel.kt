package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.coin.GetGradientCoinIconUseCase
import me.juangoncalves.mentra.domain.usecases.portfolio.RefreshPortfolioValueUseCase
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.ui.common.DisplayError

// Error with the position of the wallet being modified
typealias WalletManagementError = Pair<DisplayError, Int>

class WalletListViewModel @ViewModelInject constructor(
    coinRepository: CoinRepository,
    private val walletRepository: WalletRepository,
    private val getGradientCoinIcon: GetGradientCoinIconUseCase,
    private val refreshPortfolioValue: RefreshPortfolioValueUseCase
) : ViewModel() {

    val wallets: LiveData<List<DisplayWallet>> = coinRepository.pricesOfCoinsInUse
        .combine(walletRepository.wallets, ::mergeIntoDisplayWallets)
        .asLiveData()

    val shouldShowProgressBar: LiveData<Boolean> get() = _shouldShowProgressBar
    val generalError: LiveData<DisplayError> get() = _generalError
    val walletManagementError: LiveData<WalletManagementError> get() = _walletManagementError

    private val _shouldShowProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _generalError: MutableLiveData<DisplayError> = MutableLiveData()
    private val _walletManagementError: MutableLiveData<WalletManagementError> = MutableLiveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.wallets.collectLatest { updatePrices() }
        }
    }

    fun deleteWalletSelected(walletPosition: Int) {
        val displayWallet = wallets.value?.get(walletPosition) ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val result = walletRepository.deleteWallet(displayWallet.wallet)
            if (result.isLeft()) {
                val error = DisplayError(R.string.default_error) {
                    deleteWalletSelected(walletPosition)
                }
                _walletManagementError.postValue(Pair(error, walletPosition))
            }
        }
    }

    private fun updatePrices() {
        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowProgressBar.postValue(true)
            val result = refreshPortfolioValue()

            if (result.isLeft()) {
                val error = DisplayError(R.string.default_error, ::updatePrices)
                _generalError.postValue(error)
            }

            _shouldShowProgressBar.postValue(false)
        }
    }

    private suspend fun mergeIntoDisplayWallets(
        coinPrices: Map<Coin, Price>,
        wallets: List<Wallet>
    ): List<DisplayWallet> = wallets.map { wallet ->
        val coinPrice = coinPrices[wallet.coin] ?: Price.None
        val coinGradientIconUrl = getGradientCoinIcon(wallet.coin)
        DisplayWallet(
            wallet,
            coinGradientIconUrl,
            coinPrice.value,
            coinPrice.value * wallet.amount
        )
    }

}
