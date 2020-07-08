package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.db.daos.CoinDao
import me.juangoncalves.mentra.core.db.models.CoinModel
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

    override suspend fun getLastCoinPrice(coin: Coin, currency: Currency): Price {
        TODO("not implemented")
    }

    override suspend fun storeCoinPrice(coin: Coin, price: Price) {
        TODO("not implemented")
    }

}
