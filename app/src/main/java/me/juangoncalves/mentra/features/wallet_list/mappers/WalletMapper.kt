package me.juangoncalves.mentra.features.wallet_list.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.DeterminePrimaryIcon
import me.juangoncalves.mentra.domain.usecases.currency.ExchangePriceToPreferredCurrency
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import javax.inject.Inject

class WalletMapper @Inject constructor(
    private val determinePrimaryIcon: DeterminePrimaryIcon,
    private val exchangePriceToPreferredCurrency: ExchangePriceToPreferredCurrency
) {

    suspend fun map(wallet: Wallet, coinPrice: Price): WalletListViewState.Wallet {
        return withContext(Dispatchers.Default) {
            val exchangedCoinPrice = when (coinPrice) {
                Price.None -> Price.None
                else -> exchangePriceToPreferredCurrency.execute(coinPrice)
            }

            val walletValue = when (exchangedCoinPrice) {
                Price.None -> Price.None
                else -> {
                    val value = exchangedCoinPrice.value * wallet.amount
                    Price(value, exchangedCoinPrice.currency, exchangedCoinPrice.timestamp)
                }
            }

            WalletListViewState.Wallet(
                id = wallet.id,
                iconUrl = determinePrimaryIcon(wallet.coin).rightValue ?: "",
                value = walletValue,
                coin = WalletListViewState.Coin(wallet.coin.name, exchangedCoinPrice),
                amountOfCoin = wallet.amount
            )
        }
    }

}
