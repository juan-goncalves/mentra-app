package me.juangoncalves.pie.domain

import me.juangoncalves.pie.PiePortion

internal class PieManager {

    companion object {
        private const val MIN_PORTION = 0.025f
    }

    /** Merges all the small portions (< 2.5%) into a single one. */
    fun reducePortions(portions: Array<PiePortion>, mergedPortionText: String): Array<PiePortion> {
        val validPortions = portions.filter { it.percentage.compareTo(MIN_PORTION) > 0 }
        val invalidPortions = portions.filterNot { it.percentage.compareTo(MIN_PORTION) > 0 }
        return if (invalidPortions.isNotEmpty()) {
            val mergedPercentage = invalidPortions.sumByDouble { it.percentage }
            val validPortionsWithMerged =
                validPortions + PiePortion(
                    mergedPercentage,
                    mergedPortionText
                )
            return validPortionsWithMerged.toTypedArray()
        } else {
            portions
        }
    }

}