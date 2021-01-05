package me.juangoncalves.pie.extensions

import java.text.DecimalFormat
import kotlin.math.abs

internal fun Double.asPercentage(): String {
    val format = DecimalFormat()
    format.minimumFractionDigits = 1
    format.maximumFractionDigits = 1
    return format.format(this * 100) + "%"
}

internal infix fun Double.closeTo(other: Double): Boolean = abs(this - other) <= 0.001

internal val Double.rad: Double get() = this * Math.PI / 180