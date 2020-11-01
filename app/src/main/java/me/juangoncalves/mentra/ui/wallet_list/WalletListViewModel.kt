package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.GetActiveCoinsPriceStream
import me.juangoncalves.mentra.domain.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.domain.usecases.wallet.GetWalletListStream
import me.juangoncalves.mentra.ui.wallet_list.mappers.UIWalletMapper
import me.juangoncalves.mentra.ui.wallet_list.models.WalletListViewState
import me.juangoncalves.mentra.ui.wallet_list.models.WalletListViewState.Error

class WalletListViewModel @ViewModelInject constructor(
    private val activeCoinsPriceStream: GetActiveCoinsPriceStream,
    private val walletListStream: GetWalletListStream,
    private val refreshPortfolioValue: RefreshPortfolioValue,
    private val walletListMapper: UIWalletMapper
) : ViewModel() {

    val viewStateStream = MutableLiveData<WalletListViewState>(WalletListViewState())

    private val currentViewState: WalletListViewState get() = viewStateStream.value!!

    fun initialize() {
        loadWallets()
    }

    private fun loadWallets() = viewModelScope.launch {
        activeCoinsPriceStream()
            .combine(walletListStream(), ::mergeIntoDisplayWallets)
            .onStart { viewStateStream.value = currentViewState.copy(isLoadingWallets = true) }
            .catch {
                viewStateStream.value = currentViewState.copy(
                    error = Error.WalletsNotLoaded,
                    isLoadingWallets = false
                )
            }
            .collectLatest { wallets ->
                viewStateStream.value = currentViewState.copy(
                    wallets = wallets,
                    error = Error.None,
                    isLoadingWallets = false
                )
            }
    }

    fun refreshSelected() = viewModelScope.launch {
        viewStateStream.value = currentViewState.copy(isRefreshingPrices = true)

        val result = refreshPortfolioValue.invoke()

        viewStateStream.value = result.fold(
            left = {
                currentViewState.copy(
                    error = Error.PricesNotRefreshed(),
                    isRefreshingPrices = false
                )
            },
            right = {
                val nextError = when (currentViewState.error) {
                    is Error.PricesNotRefreshed -> Error.None
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
        walletListMapper.map(wallet, coinPrices[wallet.coin] ?: Price.None)
    }

}
