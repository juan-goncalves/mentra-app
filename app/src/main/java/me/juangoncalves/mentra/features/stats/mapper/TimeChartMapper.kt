package me.juangoncalves.mentra.features.stats.mapper

import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.usecases.preference.GetTimeUnitPreference
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.features.stats.model.TimeChartData
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class TimeChartMapper @Inject constructor(
    private val getTimeUnitPreference: GetTimeUnitPreference,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    private val weekFields = WeekFields.of(Locale.getDefault())

    suspend fun map(prices: List<Price>): TimeChartData {
        val granularity = getTimeUnitPreference(Unit).rightValue ?: TimeGranularity.Daily
        val labels = generateLabels(prices, granularity)
        val entries = generateEntries(prices)
        return TimeChartData(entries, labels, granularity)
    }

    private suspend fun generateLabels(
        prices: List<Price>,
        granularity: TimeGranularity
    ): List<String> =
        withContext(defaultDispatcher) {
            prices.map { price ->
                when (granularity) {
                    TimeGranularity.Daily -> dateFormatter.format(price.date.toLocalDate()) ?: ""
                    TimeGranularity.Weekly -> {
                        val month = price.date.month.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        )
                        val week = price.date.get(weekFields.weekOfMonth())
                        "$month ${price.date.year} - W$week"
                    }
                    TimeGranularity.Monthly -> {
                        val month = price.date.month.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        )
                        "$month ${price.date.year}"
                    }
                }
            }
        }

    private suspend fun generateEntries(prices: List<Price>): List<Entry> =
        withContext(defaultDispatcher) {
            prices.mapIndexed { index, price ->
                Entry(index.toFloat(), price.value.toFloat())
            }
        }

}