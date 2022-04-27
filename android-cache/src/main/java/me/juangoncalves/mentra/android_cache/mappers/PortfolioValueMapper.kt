package me.juangoncalves.mentra.android_cache.mappers

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import me.juangoncalves.mentra.android_cache.entities.PortfolioValueEntity
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

@Suppress("RedundantSuspendModifier")
class PortfolioValueMapper @Inject constructor() {

    suspend fun map(entity: PortfolioValueEntity?): Price? {
        if (entity == null) return null

        return Price(
            entity.valueInUSD,
            Currency.getInstance("USD"),
            entity.date.atStartOfDay().toKotlinLocalDateTime(),
        )
    }

    suspend fun map(entities: List<PortfolioValueEntity>): List<Price> = entities.mapNotNull { map(it) }

    suspend fun map(price: Price): PortfolioValueEntity = with(price) {
        PortfolioValueEntity(price.value, timestamp.toJavaLocalDateTime().toLocalDate())
    }

}
