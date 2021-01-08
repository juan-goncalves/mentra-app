package me.juangoncalves.mentra.data_layer.sources.coin

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price

interface CoinLocalDataSource {

    suspend fun getStoredCoins(): List<Coin>

    suspend fun storeCoins(coins: List<Coin>)

    suspend fun clearCoins()

    suspend fun getLastCoinPrice(coin: Coin): Price?

    suspend fun storeCoinPrices(data: List<Pair<Coin, Price>>)

    suspend fun findCoinBySymbol(symbol: String): Coin?

    suspend fun updateCoin(coin: Coin)

    fun getActiveCoinPricesStream(): Flow<Map<Coin, Price>>

}