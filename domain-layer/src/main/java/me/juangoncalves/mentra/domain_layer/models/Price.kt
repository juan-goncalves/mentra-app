package me.juangoncalves.mentra.domain_layer.models

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Price(
    val value: BigDecimal,
    val currency: Currency,
    val timestamp: LocalDateTime
) {

    companion object {
        val None: Price = Price(BigDecimal(-1), Currency.getInstance("USD"), LocalDateTime.now())
    }

}