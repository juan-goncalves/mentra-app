package me.juangoncalves.mentra.extensions

import java.text.DecimalFormat

fun Double.asCurrency(
    symbol: String = "",
    placeholderOnNegative: Boolean = true,
    forcedDecimalPlaces: Int? = null
): String {
    if (placeholderOnNegative && this < 0) {
        return "---,---.--"
    }
    val format = DecimalFormat()
    format.maximumFractionDigits = when {
        forcedDecimalPlaces != null -> forcedDecimalPlaces
        this < 10 -> 4
        this < 100_000 -> 2
        else -> 0
    }
    return "$symbol ${format.format(this)}"
}

fun Double.asCoinAmount(): String {
    val format = DecimalFormat()
    format.maximumFractionDigits = when {
        this < 10_000 -> 4
        this < 1_000_000 -> 2
        else -> 0
    }
    return format.format(this)
}