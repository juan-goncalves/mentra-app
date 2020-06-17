package me.juangoncalves.mentra.features.wallet_management.domain.entities

data class Wallet(
    val id: Int,
    val name: String,
    val coin: Coin,
    val amount: Double
)