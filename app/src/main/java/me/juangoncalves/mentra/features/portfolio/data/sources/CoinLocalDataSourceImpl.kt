package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.db.daos.CoinDao
import me.juangoncalves.mentra.core.db.models.CoinModel
import me.juangoncalves.mentra.core.db.models.CoinPriceModel
import me.juangoncalves.mentra.core.errors.PriceCacheMissException
import me.juangoncalves.mentra.core.errors.StorageException
import me.juangoncalves.mentra.features.portfolio.data.mapper.CoinMapper
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

class CoinLocalDataSourceImpl(
    private val coinDao: CoinDao,
    private val coinMapper: CoinMapper
) : CoinLocalDataSource {

    override suspend fun getStoredCoins(): List<CoinModel> = coinDao.getAll()

    override suspend fun storeCoins(coins: List<Coin>) {
        val models = coins.map(coinMapper::map)
        coinDao.insertAll(*models.toTypedArray())
    }

    override suspend fun clearCoins() = coinDao.clearAll()

    override suspend fun getLastCoinPrice(coin: Coin): Price {
        val priceModel =
            coinDao.getMostRecentCoinPrice(coin.symbol) ?: throw PriceCacheMissException()
        return Price(Currency.USD, priceModel.valueInUSD, priceModel.timestamp)
    }

    override suspend fun storeCoinPrice(coin: Coin, price: Price) {
        if (price.currency != Currency.USD) {
            throw IllegalArgumentException("Prices in the database are stored in USD")
        }
        val model = CoinPriceModel(coin.symbol, price.value, price.date)
        try {
            coinDao.insertCoinPrice(model)
        } catch (e: Exception) {
            throw StorageException("Exception when saving coin price:\n$e")
        }
    }

}
