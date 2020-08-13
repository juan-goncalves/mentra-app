package me.juangoncalves.mentra.domain.models

import java.time.LocalDateTime

data class Price(
    val currency: Currency,
    val value: Double,
    val date: LocalDateTime
) {
    companion object {
        val None: Price = Price(Currency.USD, -1.0, LocalDateTime.now())
    }
}