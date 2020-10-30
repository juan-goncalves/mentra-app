package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.GetActiveCoinsPriceStream
import me.juangoncalves.mentra.domain.usecases.coin.GetGradientCoinIcon
import me.juangoncalves.mentra.domain.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.domain.usecases.wallet.GetWalletListStream
import me.juangoncalves.mentra.extensions.rightValue

class WalletListViewModel @ViewModelInject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val activeCoinsPriceStream: GetActiveCoinsPriceStream,
    private val walletListStream: GetWalletListStream,
    private val getGradientCoinIcon: GetGradientCoinIcon,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : ViewModel() {

    val viewStateStream = MutableLiveData<WalletListViewState>(WalletListViewState())

    private val currentViewState: WalletListViewState get() = viewStateStream.value!!

    init {
        loadWallets()
    }

    private fun loadWallets() = viewModelScope.launch {
        activeCoinsPriceStream()
            .onStart { viewStateStream.value = currentViewState.copy(isLoadingWallets = true) }
            .onEach { viewStateStream.value = currentViewState.copy(isLoadingWallets = false) }
            .combine(walletListStream(), ::mergeIntoDisplayWallets)
            .collectLatest { wallets ->
                viewStateStream.value = currentViewState.copy(wallets = wallets)
            }
    }

    fun refreshSelected() = viewModelScope.launch {
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

    private suspend fun mergeIntoDisplayWallets(
        coinPrices: Map<Coin, Price>,
        wallets: List<Wallet>
    ): List<WalletListViewState.Wallet> = wallets.map { wallet ->
        withContext(defaultDispatcher) {
            val coinPrice = coinPrices[wallet.coin] ?: Price.None
            val params = GetGradientCoinIcon.Params(wallet.coin)
            val coinGradientIconUrl = getGradientCoinIcon(params).rightValue ?: ""
            val walletValue = coinPrice.value * wallet.amount

            WalletListViewState.Wallet(
                id = wallet.id,
                primaryIconUrl = coinGradientIconUrl,
                secondaryIconUrl = wallet.coin.imageUrl,
                value = walletValue,
                coin = WalletListViewState.Coin(wallet.coin.name, coinPrice.value),
                amountOfCoin = wallet.amount
            )
        }
    }

}
