package me.juangoncalves.mentra.extensions

import me.juangoncalves.mentra.domain.models.Price
import java.util.*

fun Price.asCurrencyAmount(
    forcedDecimalPlaces: Int? = null,
    absolute: Boolean = false
): String {
    val symbol = currency.getSymbol(Locale.getDefault())

    if (this == Price.None) return "$symbol ---,---.--"

    return value
        .let { if (absolute) it.abs() else it }
        .asCurrency(symbol, forcedDecimalPlaces)
}