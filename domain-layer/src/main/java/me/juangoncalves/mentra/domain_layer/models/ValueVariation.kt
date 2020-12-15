package me.juangoncalves.mentra.domain_layer.models

import me.juangoncalves.mentra.domain_layer.extensions.toPrice
import java.math.BigDecimal


data class ValueVariation(
    val difference: Price,
    val percentageChange: Double
) {

    companion object {
        val None = ValueVariation(BigDecimal.ZERO.toPrice(), 0.0)
    }

}