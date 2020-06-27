package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.errors.PriceCacheMissException
import me.juangoncalves.mentra.core.errors.StorageException
import me.juangoncalves.mentra.features.portfolio.data.models.CoinModel
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

interface CoinLocalDataSource {

    /**
     * Returns the locally stored coins.
     *
     * @throws StorageException for all problems when interacting with the data source.
     */
    suspend fun getStoredCoins(): List<CoinModel>

    /**
     * Saves a list of coins into the local data source.
     *
     * @throws StorageException for all problems when interacting with the data source.
     */
    suspend fun storeCoins(coins: List<Coin>)

    /**
     * Deletes all the stored coins in the local data source.
     *
     * @throws StorageException for all problems when interacting with the data source.
     */
    suspend fun clearCoins()

    /**
     * Finds the most recent price available in the local data source for the selected coin.
     *
     * @throws PriceCacheMissException if there isn't a price available in the local data source.
     * @throws StorageException for all problems when interacting with the data source.
     */
    suspend fun getLastCoinPrice(coin: Coin, currency: Currency): Price

    /**
     * Caches the price of a coin.
     *
     * @throws StorageException for all problems when interacting with the data source.
     */
    suspend fun storeCoinPrice(coin: Coin, price: Price)

}