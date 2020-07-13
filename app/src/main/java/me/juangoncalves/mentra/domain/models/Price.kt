package me.juangoncalves.mentra.domain.models

import java.time.LocalDateTime

data class Price(
    val currency: Currency,
    val value: Double,
    val date: LocalDateTime
)