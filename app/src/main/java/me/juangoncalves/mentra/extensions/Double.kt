package me.juangoncalves.mentra.extensions

import java.text.DecimalFormat


fun Double.asPercentage(): String {
    val format = DecimalFormat()
    format.maximumFractionDigits = 1
    format.minimumFractionDigits = format.maximumFractionDigits
    return format.format(this * 100) + "%"
}
