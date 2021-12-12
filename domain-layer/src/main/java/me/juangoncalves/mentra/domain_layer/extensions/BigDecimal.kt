package me.juangoncalves.mentra.domain_layer.extensions

import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.models.Price
import java.math.BigDecimal
import java.util.*

fun BigDecimal.toPrice(
    currency: Currency = Currency.getInstance("USD"),
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(this, currency, timestamp)
