package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.models.CoinModel
import me.juangoncalves.mentra.domain_layer.models.Coin
import javax.inject.Inject

class CoinMapper @Inject constructor() {

    fun map(coin: Coin): CoinModel = CoinModel(coin.symbol, coin.imageUrl, coin.name, coin.iconType)

    fun map(model: CoinModel): Coin =
        Coin(model.name, model.symbol, model.imageUrl, model.iconType, model.position)

}