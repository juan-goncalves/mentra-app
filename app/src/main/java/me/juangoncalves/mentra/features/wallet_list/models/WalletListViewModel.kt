package me.juangoncalves.mentra.features.wallet_list.models

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.usecases.coin.GetActiveCoinsPriceStream
import me.juangoncalves.mentra.domain_layer.usecases.currency.ExchangePriceToPreferredCurrency
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetCurrencyPreferenceStream
import me.juangoncalves.mentra.domain_layer.usecases.wallet.GetWalletListStream
import me.juangoncalves.mentra.features.wallet_list.mappers.WalletMapper
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState.Error

class WalletListViewModel @ViewModelInject constructor(
    private val activeCoinsPriceStream: GetActiveCoinsPriceStream,
    private val walletListStream: GetWalletListStream,
    private val refreshPortfolioValue: RefreshPortfolioValue,
    private val exchangePriceToPreferredCurrency: ExchangePriceToPreferredCurrency,
    private val getCurrencyPreferenceStream: GetCurrencyPreferenceStream,
    private val walletListMapper: WalletMapper
) : ViewModel() {

    val viewStateStream = MutableLiveData<WalletListViewState>(WalletListViewState())

    private val currentViewState: WalletListViewState get() = viewStateStream.value!!

    fun initialize() {
        loadWallets()

        viewModelScope.launch {
            walletListStream().collectLatest {
                refreshSelected()
            }
        }
    }

    private fun loadWallets() = viewModelScope.launch {
        activeCoinsPriceStream()
            .exchangeToPreferredCurrency()
            .mergeIntoDisplayWallets()
            .onStart { viewStateStream.value = currentViewState.copy(isLoadingWallets = true) }
            .catch {
                viewStateStream.value = currentViewState.copy(
                    error = Error.WalletsNotLoaded,
                    isLoadingWallets = false,
                    isEmpty = false
                )
            }
            .collectLatest { wallets ->
                viewStateStream.value = currentViewState.copy(
                    wallets = wallets,
                    error = Error.None,
                    isLoadingWallets = false,
                    isEmpty = wallets.isEmpty()
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

    private fun Flow<Map<Coin, Price>>.exchangeToPreferredCurrency() =
        combine(getCurrencyPreferenceStream()) { originalCoinPrices, _ ->
            originalCoinPrices.mapValues { (_, originalPrice) ->
                exchangePriceToPreferredCurrency.execute(originalPrice)
            }
        }

    private fun Flow<Map<Coin, Price>>.mergeIntoDisplayWallets() =
        combine(walletListStream()) { prices, wallets ->
            wallets.map { wallet ->
                walletListMapper.map(wallet, prices[wallet.coin] ?: Price.None)
            }
        }

}
