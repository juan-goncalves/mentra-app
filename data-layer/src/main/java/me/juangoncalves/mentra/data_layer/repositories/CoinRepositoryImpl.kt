package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data_layer.extensions.elapsedMinutes
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.*
import me.juangoncalves.mentra.domain_layer.extensions.TAG
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource,
    private val logger: MentraLogger
) : CoinRepository {

    override val pricesOfCoinsInUse: Flow<Map<Coin, Price>>
        get() = _pricesOfCoinsInUse

    private val _pricesOfCoinsInUse: Flow<Map<Coin, Price>> =
        localDataSource.getActiveCoinPricesStream()

    override suspend fun getCoins(forceNonCached: Boolean): Either<Failure, List<Coin>> =
        withContext(Dispatchers.IO) {
            val cachedCoins = try {
                localDataSource.getStoredCoins()
            } catch (e: StorageException) {
                logger.warning(TAG, "Exception while trying to get cached coins.\n$e")
                null
            }

            if (cachedCoins != null && cachedCoins.isNotEmpty() && !forceNonCached) {
                return@withContext Either.Right(cachedCoins)
            }

            return@withContext try {
                val coins = remoteDataSource.fetchCoins()
                try {
                    localDataSource.storeCoins(coins)
                } catch (e: StorageException) {
                    logger.warning(TAG, "Exception while trying to cache coins.\n$e")
                }
                Either.Right(coins)
            } catch (e: Exception) {
                val failure = when (e) {
                    is ServerException -> ServerFailure()
                    is InternetConnectionException -> InternetConnectionFailure()
                    else -> {
                        logger.error(
                            TAG,
                            "Unexpected error when fetching coins from the remote source:\n$e"
                        )
                        Failure()
                    }
                }
                Either.Left(failure)
            }
        }

    override suspend fun getCoinPrice(coin: Coin): Either<Failure, Price> =
        withContext(Dispatchers.IO) {
            try {
                val cachedPrice = localDataSource.getLastCoinPrice(coin)
                if (cachedPrice.timestamp.elapsedMinutes() <= 5) {
                    Either.Right(cachedPrice)
                } else {
                    throw PriceCacheMissException(cachedPrice)
                }
            } catch (cacheException: PriceCacheMissException) {
                try {
                    val price = remoteDataSource.fetchCoinPrice(coin)
                    // TODO: Handle storage exception
                    localDataSource.storeCoinPrice(coin, price)
                    Either.Right(price)
                } catch (e: Exception) {
                    Either.Left(FetchPriceFailure(cacheException.latestAvailablePrice))
                }
            }
        }

    override suspend fun updateCoin(coin: Coin): Either<Failure, Unit> =
        withContext(Dispatchers.IO) {
            // TODO: Handle exceptions
            localDataSource.updateCoin(coin)
            Either.Right(Unit)
        }

}