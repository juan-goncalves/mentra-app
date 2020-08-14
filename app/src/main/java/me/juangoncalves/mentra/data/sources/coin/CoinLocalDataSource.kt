package me.juangoncalves.mentra.data.sources.coin

import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price

interface CoinLocalDataSource {

    suspend fun getStoredCoins(): List<CoinModel>

    suspend fun storeCoins(coins: List<Coin>)

    suspend fun clearCoins()

    suspend fun getLastCoinPrice(coin: Coin): Price

    suspend fun storeCoinPrice(coin: Coin, price: Price)

}