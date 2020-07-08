package me.juangoncalves.mentra.features.portfolio.data.mapper

import me.juangoncalves.mentra.core.db.models.CoinModel
import me.juangoncalves.mentra.core.network.schemas.CoinSchema
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin

class CoinMapper {

    fun map(coin: Coin): CoinModel = CoinModel(coin.symbol, coin.imageUrl, coin.name)

    fun map(model: CoinModel): Coin = Coin(model.name, model.symbol, model.imageUrl)

    fun map(schema: CoinSchema): Coin = with(schema) {
        if (name.isEmpty() || symbol.isEmpty()) {
            return Coin.Invalid
        }
        return Coin(name, symbol, imageUrl)
    }

}