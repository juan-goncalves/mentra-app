package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.db.models.CoinModel
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

interface CoinLocalDataSource {

    suspend fun getStoredCoins(): List<CoinModel>

    suspend fun storeCoins(coins: List<Coin>)

    suspend fun clearCoins()

    suspend fun getLastCoinPrice(coin: Coin): Price

    suspend fun storeCoinPrice(coin: Coin, price: Price)

}