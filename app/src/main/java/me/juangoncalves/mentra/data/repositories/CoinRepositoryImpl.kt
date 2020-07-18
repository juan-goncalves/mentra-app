package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.domain.errors.*
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.elapsedMinutes
import me.juangoncalves.mentra.log.Logger
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource,
    private val coinMapper: CoinMapper,
    private val logger: Logger
) : CoinRepository {

    override suspend fun getCoins(): Either<Failure, List<Coin>> {
        val cachedCoins = try {
            localDataSource.getStoredCoins()
        } catch (e: StorageException) {
            logger.warning(TAG, "Exception while trying to get cached coins.\n$e")
            null
        }

        if (cachedCoins != null && cachedCoins.isNotEmpty()) {
            val coins = cachedCoins
                .map(coinMapper::map)
                .filterNot { it == Coin.Invalid }
            return Either.Right(coins)
        }

        return try {
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

    override suspend fun getCoinPrice(coin: Coin, currency: Currency): Either<Failure, Price> {
        return try {
            // TODO: Handle currencies
            val cachedPrice = localDataSource.getLastCoinPrice(coin)
            if (cachedPrice.date.elapsedMinutes() <= 5) {
                Either.Right(cachedPrice)
            } else {
                throw PriceCacheMissException(cachedPrice)
            }
        } catch (cacheException: PriceCacheMissException) {
            try {
                // TODO: Handle currencies
                val price = remoteDataSource.fetchCoinPrice(coin)
                // TODO: Handle storage exception
                localDataSource.storeCoinPrice(coin, price)
                Either.Right(price)
            } catch (serverException: ServerException) {
                Either.Left(FetchPriceFailure(cacheException.latestAvailablePrice))
            }
        }
    }

}