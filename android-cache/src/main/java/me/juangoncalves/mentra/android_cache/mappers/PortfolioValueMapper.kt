package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.models.PortfolioValueModel
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

@Suppress("RedundantSuspendModifier")
class PortfolioValueMapper @Inject constructor() {

    suspend fun map(model: PortfolioValueModel): Price = with(model) {
        Price(model.valueInUSD, Currency.getInstance("USD"), date.atStartOfDay())
    }

    suspend fun map(models: List<PortfolioValueModel>): List<Price> = models.map { map(it) }

    suspend fun map(price: Price): PortfolioValueModel = with(price) {
        PortfolioValueModel(price.value, timestamp.toLocalDate())
    }

}
