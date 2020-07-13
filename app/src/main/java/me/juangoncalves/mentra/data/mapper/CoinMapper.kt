package me.juangoncalves.mentra.data.mapper

import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.network.models.CoinSchema
import javax.inject.Inject

class CoinMapper @Inject constructor() {

    fun map(coin: Coin): CoinModel = CoinModel(coin.symbol, coin.imageUrl, coin.name)

    fun map(model: CoinModel): Coin = Coin(model.name, model.symbol, model.imageUrl)

    fun map(schema: CoinSchema): Coin = with(schema) {
        if (name.isEmpty() || symbol.isEmpty()) {
            return Coin.Invalid
        }
        return Coin(name, symbol, imageUrl)
    }

}