package me.juangoncalves.mentra.ui.wallet_list

import me.juangoncalves.mentra.domain.models.Wallet

data class DisplayWallet(
    val wallet: Wallet,
    val gradientIconUrl: String,
    val currentCoinPrice: Double,
    val currentWalletPrice: Double
)