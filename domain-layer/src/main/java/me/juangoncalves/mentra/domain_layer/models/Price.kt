package me.juangoncalves.mentra.domain_layer.models

import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.extensions.now
import java.math.BigDecimal
import java.util.*

data class Price(
    val value: BigDecimal,
    val currency: Currency,
    val timestamp: LocalDateTime
) {

    companion object {
        val None: Price = Price(BigDecimal(-1), Currency.getInstance("USD"), LocalDateTime.now())
        val Zero: Price = Price(BigDecimal.ZERO, Currency.getInstance("USD"), LocalDateTime.now())
    }
}