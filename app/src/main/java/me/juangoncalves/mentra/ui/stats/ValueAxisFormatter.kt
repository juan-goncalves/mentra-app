package me.juangoncalves.mentra.ui.stats

import com.github.mikephil.charting.formatter.ValueFormatter
import me.juangoncalves.mentra.extensions.asCurrency

class ValueAxisFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String = value.toDouble().asCurrency("$")

}