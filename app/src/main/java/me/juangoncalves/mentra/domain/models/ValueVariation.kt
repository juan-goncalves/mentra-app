package me.juangoncalves.mentra.domain.models

import me.juangoncalves.mentra.extensions.toPrice

data class ValueVariation(
    val difference: Price,
    val percentageChange: Double,
    val timeUnit: TimeUnit
) {

    enum class TimeUnit {
        Daily, Weekly, Monthly
    }

    companion object {
        val None = ValueVariation(0.0.toPrice(), 0.0, TimeUnit.Daily)
    }

}