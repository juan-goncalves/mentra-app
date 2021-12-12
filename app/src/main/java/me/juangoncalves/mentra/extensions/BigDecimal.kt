package me.juangoncalves.mentra.extensions

import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.extensions.now
import me.juangoncalves.mentra.domain_layer.models.Price
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

fun BigDecimal.toPrice(
    currency: Currency = Currency.getInstance("USD"),
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(this, currency, timestamp)


fun BigDecimal.asCurrency(
    symbol: String,
    forcedDecimalPlaces: Int? = null
): String {
    val format = DecimalFormat()
    format.maximumFractionDigits = when {
        forcedDecimalPlaces != null -> forcedDecimalPlaces
        this < 10.toBigDecimal() -> 4
        this < 100_000.toBigDecimal() -> 2
        else -> 0
    }
    format.minimumFractionDigits = format.maximumFractionDigits
    return "$symbol ${format.format(this)}"
}


fun BigDecimal.asCoinAmount(): String {
    val format = DecimalFormat()
    format.maximumFractionDigits = when {
        this < 1.toBigDecimal() -> 4
        this < 10_000.toBigDecimal() -> 3
        this < 1_000_000.toBigDecimal() -> 2
        else -> 0
    }
    format.minimumFractionDigits = format.maximumFractionDigits
    return format.format(this)
}