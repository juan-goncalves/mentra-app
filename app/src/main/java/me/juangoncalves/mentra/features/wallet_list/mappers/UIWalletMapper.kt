package me.juangoncalves.mentra.features.wallet_list.mappers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.DeterminePrimaryIcon
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import javax.inject.Inject

class UIWalletMapper @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val determinePrimaryIcon: DeterminePrimaryIcon
) {

    suspend fun map(wallet: Wallet, coinPrice: Price): WalletListViewState.Wallet {
        return withContext(defaultDispatcher) {
            val walletValue = coinPrice.value * wallet.amount

            WalletListViewState.Wallet(
                id = wallet.id,
                iconUrl = determinePrimaryIcon(wallet.coin).rightValue ?: "",
                value = walletValue,
                coin = WalletListViewState.Coin(wallet.coin.name, coinPrice.value),
                amountOfCoin = wallet.amount
            )
        }
    }

}
