package me.juangoncalves.mentra.domain_layer.extensions

import me.juangoncalves.mentra.domain_layer.models.Price
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

fun BigDecimal.toPrice(
    currency: Currency = Currency.getInstance("USD"),
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(this, currency, timestamp)
