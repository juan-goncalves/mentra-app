package me.juangoncalves.mentra.features.wallet_management.domain.entities

data class Money(
    val currency: Currency,
    val value: Double
)