package me.juangoncalves.pie.domain

import me.juangoncalves.pie.PiePortion
import kotlin.math.abs

internal class PiePortionValidator {

    @Throws(IllegalArgumentException::class)
    fun validatePortions(portions: Array<PiePortion>) {
        val sum = portions.sumByDouble { it.percentage }
        require(abs(sum - 1.0) <= 0.01 || sum == 0.0) {
            "The sum of all portions must be close to 1 (+- 0.01) or 0.0 (empty), it was $sum"
        }
    }

}