package me.juangoncalves.mentra.features.wallet_creation.model

import androidx.annotation.StringRes
import me.juangoncalves.mentra.domain.models.Coin

data class WalletCreationState(
    val coins: List<Coin> = emptyList(),
    val isLoadingCoins: Boolean = true,
    val selectedCoin: Coin? = null,
    val amountInput: Double? = null,
    val isSaveEnabled: Boolean = false,
    val error: Error = Error.None,
    val currentStep: Step = Step.CoinSelection,
    @StringRes val inputValidation: Int? = null
) {

    sealed class Error {
        object None : Error()

        object CoinsNotLoaded : Error()

        class WalletNotCreated : Error() {
            var wasDismissed: Boolean = false
                private set

            fun dismiss() = run { wasDismissed = true }
        }
    }

    sealed class Step {
        object CoinSelection : Step()
        object AmountInput : Step()
        object Done : Step()
    }

}