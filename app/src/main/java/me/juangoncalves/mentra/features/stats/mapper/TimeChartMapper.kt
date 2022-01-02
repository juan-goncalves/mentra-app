package me.juangoncalves.mentra.features.stats.mapper

import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.toJavaLocalDateTime
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetCurrencyPreference
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetTimeUnitPreference
import me.juangoncalves.mentra.features.stats.model.TimeChartData
import me.juangoncalves.mentra.platform.DateTimePatternProvider
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import java.util.Locale.getDefault
import javax.inject.Inject

class TimeChartMapper @Inject constructor(
    private val getTimeUnitPreference: GetTimeUnitPreference,
    private val getCurrencyPreference: GetCurrencyPreference,
    dateTimePatternProvider: DateTimePatternProvider,
) {

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

    private val shortDateFormatter = DateTimeFormatter.ofPattern(
        dateTimePatternProvider.generatePattern(getDefault(), "MM dd"),
        getDefault()
    )

    suspend fun map(prices: List<Price>): TimeChartData {
        val granularity = getTimeUnitPreference(Unit).rightValue ?: TimeGranularity.Daily
        val labels = generateLabels(prices, granularity)
        val entries = generateEntries(prices)
        val currency = getCurrencyPreference().rightValue ?: Currency.getInstance("USD")
        return TimeChartData(entries, labels, granularity, currency)
    }

    private suspend fun generateLabels(
        prices: List<Price>,
        granularity: TimeGranularity
    ): List<String> = withContext(Dispatchers.Default) {
        prices.map { price ->
            val javaDateTime = price.timestamp.toJavaLocalDateTime()

            when (granularity) {
                TimeGranularity.Daily -> dateFormatter.format(javaDateTime)
                    ?: ""
                TimeGranularity.Weekly -> {
                    val firstDay = javaDateTime.with(WeekFields.of(getDefault()).dayOfWeek(), 1L)
                    val lastDay = firstDay.with(WeekFields.of(getDefault()).dayOfWeek(), 7L)
                    "${shortDateFormatter.format(firstDay)} - ${shortDateFormatter.format(lastDay)}"
                }
                TimeGranularity.Monthly -> {
                    val month = price.timestamp.month.getDisplayName(
                        TextStyle.SHORT,
                        getDefault()
                    )
                    "$month ${price.timestamp.year}"
                }
            }
        }
    }

    private suspend fun generateEntries(prices: List<Price>): List<Entry> =
        withContext(Dispatchers.Default) {
            prices.mapIndexed { index, price ->
                Entry(index.toFloat(), price.value.toFloat())
            }
        }
}