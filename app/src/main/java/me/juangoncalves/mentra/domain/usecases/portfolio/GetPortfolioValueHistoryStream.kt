package me.juangoncalves.mentra.domain.usecases.portfolio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class GetPortfolioValueHistoryStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val preferenceRepository: PreferenceRepository
) {

    operator fun invoke(): Flow<List<Price>> {
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
            val week = price.timestamp.get(weekFields.weekOfWeekBasedYear())
            val year = price.timestamp.get(weekFields.weekBasedYear())
            Pair(week, year)
        }

        byWeek.values.toList()
    }

    private suspend fun List<Price>.toMonthly(): List<Price> = withContext(Dispatchers.Default) {
        val byMonth = associateBy { price ->
            val month = price.timestamp.monthValue
            val year = price.timestamp.year
            Pair(month, year)
        }

        byMonth.values.toList()
    }

}
