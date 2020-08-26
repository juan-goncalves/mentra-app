package me.juangoncalves.mentra.ui.add_wallet

import me.juangoncalves.mentra.domain.models.Coin

data class DisplayCoin(
    val gradientIconUrl: String,
    val coin: Coin
)