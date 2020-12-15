package me.juangoncalves.mentra.data.sources.coin

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.db.daos.CoinDao
import me.juangoncalves.mentra.db.daos.CoinPriceDao
import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.db.models.CoinPriceModel
import me.juangoncalves.mentra.domain_layer.errors.PriceCacheMissException
import me.juangoncalves.mentra.domain_layer.errors.StorageException
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

class CoinLocalDataSourceImpl @Inject constructor(
    private val coinDao: CoinDao,
    private val coinPriceDao: CoinPriceDao,
    private val coinMapper: CoinMapper
) : CoinLocalDataSource {

    override suspend fun getStoredCoins(): List<CoinModel> = orStorageException { coinDao.getAll() }

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

    override fun getActiveCoinPricesStream(): Flow<List<CoinPriceModel>> =
        coinPriceDao.getActiveCoinPricesStream()

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

}
