package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data_layer.extensions.elapsedMinutes
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.*
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
            val cachedCoins = ignoreFailure { localDataSource.getStoredCoins() } ?: emptyList()
            if (cachedCoins.isNotEmpty() && !forceNonCached) {
                cachedCoins
            } else {
                val remoteCoins = remoteDataSource.fetchCoins()
                ignoreFailure { localDataSource.storeCoins(remoteCoins) }
                remoteCoins
            }
        }

    override suspend fun getCoinPrice(coin: Coin): Either<Failure, Price> =
        errorHandler.runCatching(Dispatchers.IO) {
            val cachedPrice = ignoreFailure { localDataSource.getLastCoinPrice(coin) }
            if (cachedPrice != null && cachedPrice.timestamp.elapsedMinutes() <= 5) {
                cachedPrice
            } else {
                val remotePrice = remoteDataSource.fetchCoinPrice(coin)
                ignoreFailure { localDataSource.storeCoinPrice(coin, remotePrice) }
                remotePrice
            }
        }

    override suspend fun updateCoin(coin: Coin): Either<OldFailure, Unit> =
        withContext(Dispatchers.IO) {
            // TODO: Handle exceptions
            localDataSource.updateCoin(coin)
            Either.Right(Unit)
        }

}