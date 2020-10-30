package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
import me.juangoncalves.mentra.extensions.toPrice

class WalletListViewModel @ViewModelInject constructor(
    coinRepository: CoinRepository, // TODO: Replace with use case
    walletRepository: WalletRepository, // TODO: Replace with use case
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val getGradientCoinIcon: GetGradientCoinIcon,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : ViewModel() {

    val viewStateStream = MutableLiveData<WalletListViewState>(WalletListViewState())

    private val currentViewState: WalletListViewState get() = viewStateStream.value!!

    @ExperimentalCoroutinesApi
    private val walletListStream: Flow<List<WalletListViewState.Wallet>> =
        coinRepository.pricesOfCoinsInUse
            .onStart { viewStateStream.value = currentViewState.copy(isLoadingWallets = true) }
            .onEach { viewStateStream.value = currentViewState.copy(isLoadingWallets = false) }
            .combine(walletRepository.wallets, ::mergeIntoDisplayWallets)

    init {
        viewModelScope.launch {
            walletListStream.collectLatest { wallets ->
                viewStateStream.value = currentViewState.copy(wallets = wallets)
            }
        }
    }

    fun refreshSelected() {
        viewModelScope.launch {
            val result = refreshPortfolioValue.invoke()

            viewStateStream.value = result.fold(
                left = {
                    currentViewState.copy(
                        error = WalletListViewState.Error.PricesNotRefreshed(),
                        isRefreshingPrices = false
                    )
                },
                right = {
                    val nextError = when (currentViewState.error) {
                        is WalletListViewState.Error.PricesNotRefreshed -> WalletListViewState.Error.None
                        else -> currentViewState.error
                    }

                    currentViewState.copy(
                        error = nextError,
                        isRefreshingPrices = false
                    )
                }
            )
        }
    }

    private suspend fun mergeIntoDisplayWallets(
        coinPrices: Map<Coin, Price>,
        wallets: List<Wallet>
    ): List<WalletListViewState.Wallet> = wallets.map { wallet ->
        withContext(defaultDispatcher) {
            val coinPrice = coinPrices[wallet.coin] ?: Price.None
            val params = GetGradientCoinIcon.Params(wallet.coin)
            val coinGradientIconUrl = getGradientCoinIcon(params).rightValue ?: ""
            val walletValue = (coinPrice.value * wallet.amount).toPrice()

            WalletListViewState.Wallet(
                id = wallet.id,
                primaryIconUrl = coinGradientIconUrl,
                secondaryIconUrl = wallet.coin.imageUrl,
                value = walletValue,
                coin = WalletListViewState.Coin(wallet.coin.name, coinPrice),
                amountOfCoin = wallet.amount
            )
        }
    }

}
