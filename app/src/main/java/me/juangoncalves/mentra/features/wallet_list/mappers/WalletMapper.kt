package me.juangoncalves.mentra.features.wallet_list.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.usecases.coin.DeterminePrimaryIcon
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import javax.inject.Inject

class WalletMapper @Inject constructor(
    private val determinePrimaryIcon: DeterminePrimaryIcon
) {

    suspend fun map(wallet: Wallet, coinPrice: Price): WalletListViewState.Wallet {
        return withContext(Dispatchers.Default) {
            val walletValue = when (coinPrice) {
                Price.None -> Price.None
                else -> {
                    val value = coinPrice.value * wallet.amount
                    Price(value, coinPrice.currency, coinPrice.timestamp)
                }
            }

            val mappedWalletValue = WalletListViewState.Price(
                walletValue.value,
                walletValue.currency,
                walletValue == Price.None
            )

            val mappedCoinPrice = WalletListViewState.Price(
                coinPrice.value,
                coinPrice.currency,
                coinPrice == Price.None
            )

            WalletListViewState.Wallet(
                id = wallet.id,
                iconUrl = determinePrimaryIcon(wallet.coin).rightValue ?: "",
                value = mappedWalletValue,
                coin = WalletListViewState.Coin(wallet.coin.name, mappedCoinPrice),
                amountOfCoin = wallet.amount
            )
        }
    }

}
