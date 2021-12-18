package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlinx.datetime.toJavaLocalDateTime
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.FlowUseCase
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class GetPortfolioValueHistoryStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val preferenceRepository: PreferenceRepository
) : FlowUseCase<List<Price>> {

    override operator fun invoke(): Flow<List<Price>> {
        return preferenceRepository.valueChartTimeUnitStream
            .distinctUntilChanged { old, new -> old == new }
            .combine(portfolioRepository.portfolioDailyValueHistory) { granularity, dailyHistory ->
                when (granularity) {
                    TimeGranularity.Daily -> dailyHistory
                    TimeGranularity.Weekly -> dailyHistory.toWeekly()
                    TimeGranularity.Monthly -> dailyHistory.toMonthly()
                }
            }
    }

    private suspend fun List<Price>.toWeekly(): List<Price> = withContext(Dispatchers.Default) {
        val weekFields = WeekFields.of(Locale.getDefault())

        val byWeek = associateBy { price ->
            val javaDate = price.timestamp.toJavaLocalDateTime()
            val week = javaDate.get(weekFields.weekOfWeekBasedYear())
            val year = javaDate.get(weekFields.weekBasedYear())
            Pair(week, year)
        }

        byWeek.values.toList()
    }

    private suspend fun List<Price>.toMonthly(): List<Price> = withContext(Dispatchers.Default) {
        val byMonth = associateBy { price ->
            val javaDate = price.timestamp.toJavaLocalDateTime()
            val month = javaDate.monthValue
            val year = javaDate.year
            Pair(month, year)
        }

        byMonth.values.toList()
    }

}
