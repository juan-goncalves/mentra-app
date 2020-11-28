package me.juangoncalves.mentra.data.mapper

import me.juangoncalves.mentra.db.models.PortfolioValueModel
import me.juangoncalves.mentra.domain.models.Price
import java.util.*
import javax.inject.Inject

class PortfolioValueMapper @Inject constructor() {

    suspend fun map(model: PortfolioValueModel): Price = with(model) {
        Price(model.valueInUSD, Currency.getInstance("USD"), date.atStartOfDay())
    }

    suspend fun map(models: List<PortfolioValueModel>): List<Price> = models.map { map(it) }

    suspend fun map(price: Price): PortfolioValueModel = with(price) {
        PortfolioValueModel(price.value, timestamp.toLocalDate())
    }

}
