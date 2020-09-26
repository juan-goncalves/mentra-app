package me.juangoncalves.mentra.data.mapper

import me.juangoncalves.mentra.db.models.PortfolioValueModel
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.extensions.toPrice
import javax.inject.Inject

class PortfolioValueMapper @Inject constructor() {

    suspend fun map(model: PortfolioValueModel): Price = with(model) {
        valueInUSD.toPrice(timestamp = date.atStartOfDay())
    }

    suspend fun map(models: List<PortfolioValueModel>): List<Price> = models.map { map(it) }

    suspend fun map(price: Price): PortfolioValueModel = with(price) {
        PortfolioValueModel(value, date.toLocalDate())
    }

}
