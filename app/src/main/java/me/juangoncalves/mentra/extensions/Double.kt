package me.juangoncalves.mentra.extensions

import java.text.DecimalFormat
import kotlin.math.abs


fun Double.asPercentage(): String {
    val format = DecimalFormat()
    format.maximumFractionDigits = 1
    format.minimumFractionDigits = format.maximumFractionDigits
    return format.format(this * 100) + "%"
}

infix fun Double.closeTo(other: Double): Boolean = abs(this - other) <= 0.001
