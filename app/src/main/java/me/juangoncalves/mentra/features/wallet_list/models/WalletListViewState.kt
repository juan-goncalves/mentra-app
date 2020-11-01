package me.juangoncalves.mentra.features.wallet_list.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class WalletListViewState(
    val isLoadingWallets: Boolean = true,
    val isRefreshingPrices: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val error: Error = Error.None
) {

    sealed class Error {
        object None : Error()

        object WalletsNotLoaded : Error()

        class PricesNotRefreshed : Error() {
            var wasDismissed: Boolean = false
                private set

            fun dismiss() = run { wasDismissed = true }
        }
    }

    @Parcelize
    data class Wallet(
        val id: Long,
        val primaryIconUrl: String,
        val secondaryIconUrl: String,
        val value: Double,
        val coin: Coin,
        val amountOfCoin: Double
    ) : Parcelable

    @Parcelize
    data class Coin(
        val name: String,
        val value: Double
    ) : Parcelable

}