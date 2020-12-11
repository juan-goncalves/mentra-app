package me.juangoncalves.mentra.features.stats.ui

import com.github.mikephil.charting.formatter.ValueFormatter
import me.juangoncalves.mentra.extensions.asCurrency
import java.util.*

class ValueAxisFormatter(private val currency: Currency) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toBigDecimal().asCurrency(currency.symbol)
    }

}