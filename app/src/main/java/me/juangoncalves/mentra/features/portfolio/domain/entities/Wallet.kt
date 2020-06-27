package me.juangoncalves.mentra.features.portfolio.domain.entities

data class Wallet(
    val id: Int,
    val name: String,
    val coin: Coin,
    val amount: Double
)