package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.coin.GetGradientCoinIconUseCase
import me.juangoncalves.mentra.domain.usecases.portfolio.RefreshPortfolioValueUseCase
import me.juangoncalves.mentra.ui.common.DefaultErrorHandlingViewModel
import me.juangoncalves.mentra.ui.common.DisplayError

// Error with the position of the wallet being modified
typealias WalletManagementError = Pair<DisplayError, Int>

class WalletListViewModel @ViewModelInject constructor(
    coinRepository: CoinRepository,
    private val walletRepository: WalletRepository,
    private val getGradientCoinIcon: GetGradientCoinIconUseCase,
    private val refreshPortfolioValue: RefreshPortfolioValueUseCase
) : DefaultErrorHandlingViewModel() {

    val wallets: LiveData<List<DisplayWallet>> = coinRepository.pricesOfCoinsInUse
        .combine(walletRepository.wallets, ::mergeIntoDisplayWallets)
        .showRetrySnackbarOnError()
        .asLiveData()

    val shouldShowProgressBar: LiveData<Boolean> get() = _shouldShowProgressBar
    val walletManagementError: LiveData<WalletManagementError> get() = _walletManagementError

    private val _shouldShowProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _walletManagementError: MutableLiveData<WalletManagementError> = MutableLiveData()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.wallets.collectLatest { updatePrices() }
        }
    }

    private fun updatePrices() {
        refreshPortfolioValue.prepare()
            .beforeInvoke { _shouldShowProgressBar.postValue(true) }
            .afterInvoke { _shouldShowProgressBar.postValue(false) }
            .withDispatcher(Dispatchers.IO)
            .run(Unit)
    }

    @Suppress("RedundantSuspendModifier")
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
