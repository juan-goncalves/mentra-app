package me.juangoncalves.pie.extensions

import java.text.DecimalFormat
import kotlin.math.abs

internal fun Double.asPercentage(): String {
    val format = DecimalFormat()
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return format.format(this * 100) + "%"
}

internal infix fun Double.closeTo(other: Double): Boolean = abs(this - other) <= 0.001

internal fun Double.toRadians(): Double = this * Math.PI / 180