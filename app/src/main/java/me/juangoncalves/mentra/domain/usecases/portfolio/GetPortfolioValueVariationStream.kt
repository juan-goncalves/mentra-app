package me.juangoncalves.mentra.domain.usecases.portfolio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain.models.ValueVariation
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.extensions.toPrice
import javax.inject.Inject

class GetPortfolioValueVariationStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) {

    // TODO: Support the different time units

    operator fun invoke(timeUnit: ValueVariation.TimeUnit): Flow<ValueVariation> =
        portfolioRepository.portfolioValueHistory.map { prices ->
            if (prices.size < 2) return@map ValueVariation.None

            val latestValue = prices[prices.lastIndex]
            val previousDayValue = prices[prices.lastIndex - 1]
            val percentChange = latestValue.value / previousDayValue.value - 1
            val valueDifference = latestValue.value - previousDayValue.value

            ValueVariation(valueDifference.toPrice(), percentChange, timeUnit)
        }

}