package me.juangoncalves.mentra.features.wallet_management.domain.entities

data class Coin(
    val name: String,
    val symbol: String,
    val imageUrl: String
    // TODO: Maybe add a price attribute
)