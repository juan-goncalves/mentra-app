package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.data_layer.extensions.elapsedMinutes
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.ignoringFailure
import me.juangoncalves.mentra.domain_layer.errors.runCatching
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource,
    private val errorHandler: ErrorHandler
) : CoinRepository {

    override val pricesOfCoinsInUse: Flow<Map<Coin, Price>>
        get() = _pricesOfCoinsInUse

    private val _pricesOfCoinsInUse: Flow<Map<Coin, Price>> by lazy { localDataSource.getActiveCoinPricesStream() }

    override suspend fun getCoins(forceNonCached: Boolean): Either<Failure, List<Coin>> =
        errorHandler.runCatching(Dispatchers.IO) {
            val cachedCoins = ignoringFailure { localDataSource.getStoredCoins() } ?: emptyList()
            if (cachedCoins.isNotEmpty() && !forceNonCached) {
                cachedCoins
            } else {
                val remoteCoins = remoteDataSource.fetchCoins()
                ignoringFailure { localDataSource.storeCoins(remoteCoins) }
                remoteCoins
            }
        }

    override suspend fun getCoinPrice(coin: Coin): Either<Failure, Price> =
        errorHandler.runCatching(Dispatchers.IO) {
            val cachedPrice = ignoringFailure { localDataSource.getLastCoinPrice(coin) }
            if (cachedPrice != null && cachedPrice.timestamp.elapsedMinutes() <= 5) {
                cachedPrice
            } else {
                val remotePrice = remoteDataSource.fetchCoinPrice(coin)
                ignoringFailure { localDataSource.storeCoinPrice(coin, remotePrice) }
                remotePrice
            }
        }

    override suspend fun updateCoin(coin: Coin): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.updateCoin(coin)
        }

}