package me.juangoncalves.mentra.ui.stats

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class PercentageFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String = "${(value * 100).roundToInt()}%"

}