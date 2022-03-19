package me.juangoncalves.mentra.features.wallet_list.mappers

import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.usecases.coin.GetPrimaryCoinIcon
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import javax.inject.Inject

// todo: update unit tests
class WalletMapper @Inject constructor(
    private val getPrimaryCoinIcon: GetPrimaryCoinIcon,
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

            val iconUrls = getPrimaryCoinIcon(wallet.coin).fold(
                left = { "" },
                right = { it }
            )

            WalletListViewState.Wallet(
                id = wallet.id,
                iconUrl = iconUrls,
                value = mappedWalletValue,
                coin = WalletListViewState.Coin(wallet.coin.name, mappedCoinPrice),
                amountOfCoin = wallet.amount
            )
        }
    }

}
