package me.juangoncalves.mentra.domain.models

data class Wallet(
    val id: Int,
    val name: String,
    val coin: Coin,
    val amount: Double
)