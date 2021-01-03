package me.juangoncalves.mentra.features.wallet_list.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

data class WalletListViewState(
    val isLoadingWallets: Boolean = true,
    val isRefreshingPrices: Boolean = false,
    val isEmpty: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val error: Error = Error.None
) {

    sealed class Error {
        object None : Error()
        object WalletsNotLoaded : Error()
    }

    @Parcelize
    data class Price(
        val value: BigDecimal,
        val currency: Currency,
        val isPlaceholder: Boolean
    ) : Parcelable

    @Parcelize
    data class Wallet(
        val id: Long,
        val iconUrl: String,
        val value: Price,
        val coin: Coin,
        val amountOfCoin: BigDecimal
    ) : Parcelable

    @Parcelize
    data class Coin(
        val name: String,
        val value: Price
    ) : Parcelable

}