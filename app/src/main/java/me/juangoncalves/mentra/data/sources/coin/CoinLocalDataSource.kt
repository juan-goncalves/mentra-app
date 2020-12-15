package me.juangoncalves.mentra.data.sources.coin

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.db.models.CoinPriceModel
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price

interface CoinLocalDataSource {

    suspend fun getStoredCoins(): List<CoinModel>

    suspend fun storeCoins(coins: List<Coin>)

    suspend fun clearCoins()

    suspend fun getLastCoinPrice(coin: Coin): Price

    suspend fun storeCoinPrice(coin: Coin, price: Price)

    suspend fun findCoinBySymbol(symbol: String): Coin?

    suspend fun updateCoin(coin: Coin)

    fun getActiveCoinPricesStream(): Flow<List<CoinPriceModel>>

}