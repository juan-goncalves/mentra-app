package me.juangoncalves.mentra.features.wallet_list.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.DeterminePrimaryIcon
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import javax.inject.Inject

class UIWalletMapper @Inject constructor(
    private val determinePrimaryIcon: DeterminePrimaryIcon
) {

    suspend fun map(wallet: Wallet, coinPrice: Price): WalletListViewState.Wallet {
        return withContext(Dispatchers.Default) {
            val walletValue = coinPrice.value * wallet.amount

            WalletListViewState.Wallet(
                id = wallet.id,
                iconUrl = determinePrimaryIcon(wallet.coin).rightValue ?: "",
                value = Price(walletValue, coinPrice.currency, coinPrice.timestamp),
                coin = WalletListViewState.Coin(wallet.coin.name, coinPrice),
                amountOfCoin = wallet.amount
            )
        }
    }

}
