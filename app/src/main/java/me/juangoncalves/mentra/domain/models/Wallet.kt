package me.juangoncalves.mentra.domain.models

data class Wallet(
    val coin: Coin,
    val amount: Double,
    val id: Long = 0
)