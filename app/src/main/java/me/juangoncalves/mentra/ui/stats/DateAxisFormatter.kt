package me.juangoncalves.mentra.ui.stats

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DateAxisFormatter(
    private val indexToDates: Map<Float, LocalDate>
) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        val date = indexToDates[value]
        if (date != null) {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            return formatter.format(date)
        }

        return ""
    }

}