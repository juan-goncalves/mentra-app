package me.juangoncalves.mentra.ui.wallet_creation

import me.juangoncalves.mentra.domain.models.Coin

data class DisplayCoin(
    val gradientIconUrl: String,
    val coin: Coin
)