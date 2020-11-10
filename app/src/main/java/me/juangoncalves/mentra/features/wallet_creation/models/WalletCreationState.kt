package me.juangoncalves.mentra.features.wallet_creation.models

import androidx.annotation.StringRes
import me.juangoncalves.mentra.domain.models.Coin

data class WalletCreationState(
    val coins: List<Coin> = emptyList(),
    val isLoadingCoins: Boolean = true,
    val selectedCoin: Coin? = null,
    val amountInput: Double? = null,
    val currentStep: Step = Step.CoinSelection,
    @StringRes val inputValidation: Int? = null,
    val isSaveEnabled: Boolean = false,
    val error: Error = Error.None
) {

    sealed class Error {
        object None : Error()
        object CoinsNotLoaded : Error()
    }

    sealed class Step {
        object CoinSelection : Step()
        object AmountInput : Step()
        object Done : Step()
    }

}