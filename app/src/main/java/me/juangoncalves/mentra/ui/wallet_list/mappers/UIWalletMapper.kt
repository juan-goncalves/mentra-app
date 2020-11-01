package me.juangoncalves.mentra.ui.wallet_list.mappers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.coin.GetGradientCoinIcon
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.ui.wallet_list.models.WalletListViewState
import javax.inject.Inject

class UIWalletMapper @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val getGradientCoinIcon: GetGradientCoinIcon
) {

    suspend fun map(wallet: Wallet, coinPrice: Price): WalletListViewState.Wallet {
        return withContext(defaultDispatcher) {
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
