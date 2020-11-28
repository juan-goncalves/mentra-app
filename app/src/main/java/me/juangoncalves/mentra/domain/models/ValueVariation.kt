package me.juangoncalves.mentra.domain.models

import me.juangoncalves.mentra.extensions.toPrice
import java.math.BigDecimal


data class ValueVariation(
    val difference: Price,
    val percentageChange: Double,
    val timeUnit: TimeUnit
) {

    enum class TimeUnit {
        Daily, Weekly, Monthly
    }

    companion object {
        val None = ValueVariation(BigDecimal.ZERO.toPrice(), 0.0, TimeUnit.Daily)
    }

}