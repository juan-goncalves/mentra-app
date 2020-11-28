package me.juangoncalves.mentra.features.stats.ui

import com.github.mikephil.charting.formatter.ValueFormatter
import me.juangoncalves.mentra.extensions.asCurrency

class ValueAxisFormatter : ValueFormatter() {

    // TODO: Handle different currencies
    override fun getFormattedValue(value: Float): String = value.toBigDecimal().asCurrency("$")

}