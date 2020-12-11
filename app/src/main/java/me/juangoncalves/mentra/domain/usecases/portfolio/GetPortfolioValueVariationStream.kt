package me.juangoncalves.mentra.domain.usecases.portfolio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain.models.ValueVariation
import me.juangoncalves.mentra.extensions.toPrice
import java.math.BigDecimal
import javax.inject.Inject

class GetPortfolioValueVariationStream @Inject constructor(
    private val getPortfolioValueHistoryStream: GetPortfolioValueHistoryStream
) {

    operator fun invoke(): Flow<ValueVariation> = getPortfolioValueHistoryStream().map { prices ->
        if (prices.size < 2) return@map ValueVariation.None

        val lastPrice = prices[prices.lastIndex]
        val previousPrice = prices[prices.lastIndex - 1]

        require(lastPrice.currency == previousPrice.currency)

        val percentChange = lastPrice.value / previousPrice.value - BigDecimal.ONE
        val valueDifference = lastPrice.value - previousPrice.value
        val diffPrice = valueDifference.toPrice(currency = lastPrice.currency)

        ValueVariation(diffPrice, percentChange.toDouble())
    }

}