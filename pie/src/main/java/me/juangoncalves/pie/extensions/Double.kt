package me.juangoncalves.pie.extensions

import java.text.DecimalFormat

internal fun Double.asPercentage(): String {
    val format = DecimalFormat()
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return format.format(this * 100) + "%"
}
