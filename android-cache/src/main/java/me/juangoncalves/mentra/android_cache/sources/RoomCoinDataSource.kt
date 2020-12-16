package me.juangoncalves.mentra.android_cache.sources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.android_cache.daos.CoinDao
import me.juangoncalves.mentra.android_cache.daos.CoinPriceDao
import me.juangoncalves.mentra.android_cache.mappers.CoinMapper
import me.juangoncalves.mentra.android_cache.models.CoinPriceModel
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.PriceCacheMissException
import me.juangoncalves.mentra.domain_layer.errors.StorageException
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

class RoomCoinDataSource @Inject constructor(
    private val coinDao: CoinDao,
    private val coinPriceDao: CoinPriceDao,
    private val coinMapper: CoinMapper
) : CoinLocalDataSource {

    override suspend fun getStoredCoins(): List<Coin> = orStorageException {
        coinDao.getAll()
            .map(coinMapper::map)
            .filterNot { it == Coin.Invalid }
    }

    override suspend fun storeCoins(coins: List<Coin>) = orStorageException {
        val models = coins.map(coinMapper::map)
        coinDao.insertAll(*models.toTypedArray())
    }

    override suspend fun clearCoins() = orStorageException { coinDao.clearAll() }

    override suspend fun getLastCoinPrice(coin: Coin): Price {
        val model = orStorageException {
            coinPriceDao.getMostRecentCoinPrice(coin.symbol)
        } ?: throw PriceCacheMissException()

        return Price(model.valueInUSD, Currency.getInstance("USD"), model.timestamp)
    }

    override suspend fun storeCoinPrice(coin: Coin, price: Price) {
        require(price.currency == Currency.getInstance("USD")) {
            "Prices in the database are stored in USD"
        }

        val model = CoinPriceModel(coin.symbol, price.value, price.timestamp)
        return orStorageException("Exception when saving coin price.") {
            coinPriceDao.insertCoinPrice(model)
        }
    }

    override suspend fun findCoinBySymbol(symbol: String): Coin? {
        return orStorageException("Exception when finding coin by symbol.") {
            val coin = coinDao.getCoinBySymbol(symbol)
            if (coin != null) coinMapper.map(coin) else null
        }
    }

    override suspend fun updateCoin(coin: Coin) = orStorageException {
        val model = coinMapper.map(coin)
        coinDao.update(model)
    }

    override fun getActiveCoinPricesStream(): Flow<Map<Coin, Price>> =
        coinPriceDao.getActiveCoinPricesStream().associateByCoin()

    @Throws(StorageException::class)
    private suspend fun <T> orStorageException(
        message: String = "",
        execute: suspend () -> T
    ): T {
        return try {
            execute()
        } catch (e: Exception) {
            throw StorageException("$message\n$e")
        }
    }

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
