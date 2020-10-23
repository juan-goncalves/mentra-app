package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
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
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val getGradientCoinIcon: GetGradientCoinIcon,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    @ExperimentalCoroutinesApi
    val wallets: LiveData<List<DisplayWallet>> = coinRepository.pricesOfCoinsInUse
        .onStart { _shouldShowWalletLoadingIndicator.value = true }
        .onEach { _shouldShowWalletLoadingIndicator.value = false }
        .combine(walletRepository.wallets, ::mergeIntoDisplayWallets)
        .asLiveData()

    val walletManagementError: LiveData<WalletManagementError> get() = _walletManagementError
    val shouldShowRefreshIndicator: LiveData<Boolean> get() = _shouldShowRefreshIndicator
    val shouldShowWalletLoadingIndicator: LiveData<Boolean> get() = _shouldShowWalletLoadingIndicator

    private val _walletManagementError: MutableLiveData<WalletManagementError> = MutableLiveData()
    private val _shouldShowRefreshIndicator: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _shouldShowWalletLoadingIndicator: MutableLiveData<Boolean> = MutableLiveData(true)


    fun refreshSelected() {
        refreshPortfolioValue.executor()
            .withDispatcher(Dispatchers.IO)
            .inScope(viewModelScope)
            .beforeInvoke { _shouldShowRefreshIndicator.postValue(true) }
            .afterInvoke { _shouldShowRefreshIndicator.postValue(false) }
            .onFailurePublishFleetingError()
            .run()
    }

    private suspend fun mergeIntoDisplayWallets(
        coinPrices: Map<Coin, Price>,
        wallets: List<Wallet>
    ): List<DisplayWallet> = wallets.map { wallet ->
        withContext(defaultDispatcher) {
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

}
