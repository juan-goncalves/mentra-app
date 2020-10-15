package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.coin.GetGradientCoinIcon
import me.juangoncalves.mentra.domain.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.ui.common.*

// Error with the position of the wallet being modified
typealias WalletManagementError = Pair<DisplayError, Int>

class WalletListViewModel @ViewModelInject constructor(
    coinRepository: CoinRepository,
    private val walletRepository: WalletRepository,
    private val getGradientCoinIcon: GetGradientCoinIcon,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val wallets: LiveData<List<DisplayWallet>> = coinRepository.pricesOfCoinsInUse
        .combine(walletRepository.wallets, ::mergeIntoDisplayWallets)
        .onFailurePublishFleetingError()
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
        refreshPortfolioValue.executor()
            .withDispatcher(Dispatchers.IO)
            .inScope(viewModelScope)
            .beforeInvoke { _shouldShowProgressBar.postValue(true) }
            .afterInvoke { _shouldShowProgressBar.postValue(false) }
            .onFailurePublishFleetingError()
            .run()
    }

    @Suppress("RedundantSuspendModifier")
    private suspend fun mergeIntoDisplayWallets(
        coinPrices: Map<Coin, Price>,
        wallets: List<Wallet>
    ): List<DisplayWallet> = wallets.map { wallet ->
        val coinPrice = coinPrices[wallet.coin] ?: Price.None
        val params = GetGradientCoinIcon.Params(wallet.coin)
        val coinGradientIconUrl = getGradientCoinIcon(params).rightValue ?: ""

        DisplayWallet(
            wallet,
            coinGradientIconUrl,
            coinPrice.value,
            coinPrice.value * wallet.amount
        )
    }

}
