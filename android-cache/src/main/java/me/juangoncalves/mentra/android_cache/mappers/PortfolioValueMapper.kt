package me.juangoncalves.mentra.android_cache.mappers

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import me.juangoncalves.mentra.android_cache.models.PortfolioValueModel
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

@Suppress("RedundantSuspendModifier")
class PortfolioValueMapper @Inject constructor() {

    suspend fun map(model: PortfolioValueModel?): Price? {
        if (model == null) return null

        return Price(
            model.valueInUSD,
            Currency.getInstance("USD"),
            model.date.atStartOfDay().toKotlinLocalDateTime(),
        )
    }

    suspend fun map(models: List<PortfolioValueModel>): List<Price> = models.mapNotNull { map(it) }

    suspend fun map(price: Price): PortfolioValueModel = with(price) {
        PortfolioValueModel(price.value, timestamp.toJavaLocalDateTime().toLocalDate())
    }

}
