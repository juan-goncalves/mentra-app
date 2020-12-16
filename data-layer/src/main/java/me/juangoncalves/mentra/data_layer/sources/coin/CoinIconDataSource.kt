package me.juangoncalves.mentra.data_layer.sources.coin

import me.juangoncalves.mentra.domain_layer.models.Coin

interface CoinIconDataSource {

    suspend fun getAlternativeIconFor(coin: Coin): String?

}