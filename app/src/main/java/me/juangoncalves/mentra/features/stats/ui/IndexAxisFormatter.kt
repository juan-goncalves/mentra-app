package me.juangoncalves.mentra.features.stats.ui

import com.github.mikephil.charting.formatter.ValueFormatter

class IndexAxisFormatter(
    private val labels: List<String>
) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in 0..labels.lastIndex) labels[index] else ""
    }

}