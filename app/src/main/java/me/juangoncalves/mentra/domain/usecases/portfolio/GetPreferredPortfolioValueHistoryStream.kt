package me.juangoncalves.mentra.domain.usecases.portfolio

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class GetPreferredPortfolioValueHistoryStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val preferenceRepository: PreferenceRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
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

    private suspend fun List<Price>.toWeekly(): List<Price> = withContext(defaultDispatcher) {
        val weekFields = WeekFields.of(Locale.getDefault())

        val byWeek = associateBy { price ->
            val week = price.date.get(weekFields.weekOfWeekBasedYear())
            val year = price.date.get(weekFields.weekBasedYear())
            Pair(week, year)
        }

        byWeek.values.toList()
    }

    private suspend fun List<Price>.toMonthly(): List<Price> = withContext(defaultDispatcher) {
        val byMonth = associateBy { price ->
            val month = price.date.monthValue
            val year = price.date.year
            Pair(month, year)
        }

        byMonth.values.toList()
    }

}
