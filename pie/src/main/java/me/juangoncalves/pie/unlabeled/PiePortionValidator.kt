package me.juangoncalves.pie.unlabeled

import me.juangoncalves.pie.PiePortion
import kotlin.math.abs

internal class PiePortionValidator {

    @Throws(IllegalArgumentException::class)
    fun validatePortions(portions: Array<PiePortion>) {
        val sum = portions.sumByDouble { it.percentage }
        require(abs(sum - 1.0) <= 0.01) {
            "The sum of all portions must be close to 1 (+- 0.01), it was $sum"
        }
        val emptyPortions = portions.filter { it.percentage == 0.0 }
        require(emptyPortions.isEmpty()) { "Empty portions are not valid." }
    }

}