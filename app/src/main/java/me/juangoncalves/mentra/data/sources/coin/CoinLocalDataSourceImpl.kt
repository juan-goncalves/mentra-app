package me.juangoncalves.mentra.data.sources.coin

import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.db.daos.CoinDao
import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.db.models.CoinPriceModel
import me.juangoncalves.mentra.domain.errors.PriceCacheMissException
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import javax.inject.Inject

class CoinLocalDataSourceImpl @Inject constructor(
    private val coinDao: CoinDao,
    private val coinMapper: CoinMapper
) : CoinLocalDataSource {

    override suspend fun getStoredCoins(): List<CoinModel> = orStorageException { coinDao.getAll() }

    override suspend fun storeCoins(coins: List<Coin>) = orStorageException {
        val models = coins.map(coinMapper::map)
        coinDao.insertAll(*models.toTypedArray())
    }

    override suspend fun clearCoins() = orStorageException { coinDao.clearAll() }

    override suspend fun getLastCoinPrice(coin: Coin): Price {
        val priceModel = orStorageException {
            coinDao.getMostRecentCoinPrice(coin.symbol)
        } ?: throw PriceCacheMissException()
        return Price(Currency.USD, priceModel.valueInUSD, priceModel.timestamp)
    }

    override suspend fun storeCoinPrice(coin: Coin, price: Price) {
        if (price.currency != Currency.USD) {
            throw IllegalArgumentException("Prices in the database are stored in USD")
        }
        val model = CoinPriceModel(coin.symbol, price.value, price.date)
        return orStorageException("Exception when saving coin price.") {
            coinDao.insertCoinPrice(model)
        }
    }

    override suspend fun findCoinBySymbol(symbol: String): Coin? {
        return orStorageException("Exception when finding coin by symbol.") {
            val coin = coinDao.getCoinBySymbol(symbol)
            if (coin != null) coinMapper.map(coin) else null
        }
    }

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
