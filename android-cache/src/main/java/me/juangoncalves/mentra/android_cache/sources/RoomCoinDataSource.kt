package me.juangoncalves.mentra.android_cache.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.android_cache.daos.CoinDao
import me.juangoncalves.mentra.android_cache.daos.CoinPriceDao
import me.juangoncalves.mentra.android_cache.mappers.CoinMapper
import me.juangoncalves.mentra.android_cache.models.CoinPriceModel
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

class RoomCoinDataSource @Inject constructor(
    private val coinDao: CoinDao,
    private val coinPriceDao: CoinPriceDao,
    private val coinMapper: CoinMapper
) : CoinLocalDataSource {

    override suspend fun getStoredCoins(): List<Coin> = withContext(Dispatchers.Default) {
        coinDao.getAll()
            .map(coinMapper::map)
            .filterNot { it == Coin.Invalid }
    }

    override suspend fun storeCoins(coins: List<Coin>) = withContext(Dispatchers.Default) {
        val models = coins.map(coinMapper::map)
        coinDao.insertAll(*models.toTypedArray())
    }

    override suspend fun clearCoins() = coinDao.clearAll()

    override suspend fun getLastCoinPrice(coin: Coin): Price? = withContext(Dispatchers.Default) {
        when (val model = coinPriceDao.getMostRecentCoinPrice(coin.symbol)) {
            null -> null
            else -> Price(model.valueInUSD, Currency.getInstance("USD"), model.timestamp)
        }
    }

    override suspend fun storeCoinPrice(coin: Coin, price: Price) =
        withContext(Dispatchers.Default) {
            require(price.currency == Currency.getInstance("USD")) {
                "Prices in the database are stored in USD"
            }

            val model = CoinPriceModel(coin.symbol, price.value, price.timestamp)
            coinPriceDao.insertCoinPrice(model)
        }

    override suspend fun findCoinBySymbol(symbol: String): Coin? =
        withContext(Dispatchers.Default) {
            val coin = coinDao.getCoinBySymbol(symbol)
            if (coin != null) coinMapper.map(coin) else null
        }

    override suspend fun updateCoin(coin: Coin) = withContext(Dispatchers.Default) {
        val model = coinMapper.map(coin)
        coinDao.update(model)
    }

    override fun getActiveCoinPricesStream(): Flow<Map<Coin, Price>> =
        coinPriceDao.getActiveCoinPricesStream().associateByCoin()

    private fun Flow<List<CoinPriceModel>>.associateByCoin(): Flow<Map<Coin, Price>> = debounce(500)
        .map { prices ->
            val coinPricesMap = hashMapOf<Coin, Price>()

            prices.forEach { priceModel ->
                val coin = findCoinBySymbol(priceModel.coinSymbol) ?: return@forEach

                coinPricesMap[coin] = Price(
                    priceModel.valueInUSD,
                    Currency.getInstance("USD"),
                    priceModel.timestamp
                )
            }

            coinPricesMap
        }

}
