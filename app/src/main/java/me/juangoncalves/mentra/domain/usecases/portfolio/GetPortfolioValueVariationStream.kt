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
        portfolioRepository.portfolioDailyValueHistory.map { prices ->
            if (prices.size < 2) return@map ValueVariation.None

            val latestValue = prices[prices.lastIndex].value.toDouble()
            val previousDayValue = prices[prices.lastIndex - 1].value.toDouble()
            val percentChange = latestValue / previousDayValue - 1
            val valueDifference = latestValue - previousDayValue
            // TODO: Handle currencies appropriately
            val diffPrice = valueDifference.toBigDecimal().toPrice()
            ValueVariation(diffPrice, percentChange, timeUnit)
        }

}