package me.juangoncalves.mentra.domain.models

import java.io.Serializable

data class Wallet(
    val coin: Coin,
    val amount: Double,
    val id: Long = 0
) : Serializable