package me.juangoncalves.mentra.ui.wallet_list

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import me.juangoncalves.mentra.domain.models.Price

data class WalletListViewState(
    val isLoadingWallets: Boolean = true,
    val isRefreshingPrices: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val error: Error = Error.None
) {

    sealed class Error {

        var wasDismissed: Boolean = false
            private set

        fun dismiss() = run { wasDismissed = true }

        object None : Error()
        class PricesNotRefreshed : Error()
        class UpdatesNotSaved : Error()
        class DeleteNotSaved : Error()
    }

    @Parcelize
    data class Wallet(
        val id: Long,
        val primaryIconUrl: String,
        val secondaryIconUrl: String,
        val value: Price,
        val coin: Coin,
        val amountOfCoin: Double
    ) : Parcelable

    @Parcelize
    data class Coin(
        val name: String,
        val value: Price
    ) : Parcelable

}