package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
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
    walletRepository: WalletRepository,
    private val getGradientCoinIcon: GetGradientCoinIcon,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val wallets: LiveData<List<DisplayWallet>> = coinRepository.pricesOfCoinsInUse
        .combine(walletRepository.wallets, ::mergeIntoDisplayWallets)
        .asLiveData()

    val walletManagementError: LiveData<WalletManagementError> get() = _walletManagementError
    val shouldShowRefreshIndicator: LiveData<Boolean> get() = _shouldShowRefreshIndicator

    private val _walletManagementError: MutableLiveData<WalletManagementError> = MutableLiveData()
    private val _shouldShowRefreshIndicator: MutableLiveData<Boolean> = MutableLiveData(false)


    fun refreshSelected() {
        refreshPortfolioValue.executor()
            .withDispatcher(Dispatchers.IO)
            .inScope(viewModelScope)
            .beforeInvoke { _shouldShowRefreshIndicator.postValue(true) }
            .afterInvoke { _shouldShowRefreshIndicator.postValue(false) }
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
