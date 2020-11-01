package me.juangoncalves.mentra.features.stats

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DateAxisFormatter(
    private val indexToDates: Map<Int, LocalDate>
) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        val date = indexToDates[value.toInt()]
        if (date != null) {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            return formatter.format(date)
        }

        return "-"
    }

}