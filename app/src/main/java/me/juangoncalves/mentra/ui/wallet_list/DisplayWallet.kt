package me.juangoncalves.mentra.ui.wallet_list

import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet

data class DisplayWallet(
    val wallet: Wallet,
    val gradientIconUrl: String,
    val currentCoinPrice: Double,
    val currentWalletPrice: Double,
    val historicPrice: List<Price>

    // In this class we could add an attribute to show a warning, for example if the price fetching
    // fails completely we can show an error indicator over the coin image, and if we didn't manage
    // to get the latest price but we had one cached, we can show a warning over the coin image
    // (Maybe a red border over the coin image for errors and a yellow one for warnings)
)