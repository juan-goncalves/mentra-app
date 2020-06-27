package me.juangoncalves.mentra.features.wallet_management.domain.entities

import java.time.LocalDateTime

data class Price(
    val currency: Currency,
    val value: Double,
    val date: LocalDateTime
)