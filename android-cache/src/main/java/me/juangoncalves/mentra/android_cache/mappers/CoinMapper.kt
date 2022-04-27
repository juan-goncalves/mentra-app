package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.entities.CoinEntity
import me.juangoncalves.mentra.domain_layer.models.Coin
import javax.inject.Inject

class CoinMapper @Inject constructor() {

    fun map(coin: Coin): CoinEntity =
        CoinEntity(coin.symbol, coin.imageUrl, coin.name, coin.position)

    fun map(entity: CoinEntity): Coin =
        Coin(entity.name, entity.symbol, entity.imageUrl, entity.position)

}