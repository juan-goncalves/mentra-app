package me.juangoncalves.mentra.data.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.db.daos.CoinPriceDao
import me.juangoncalves.mentra.db.models.CoinPriceModel
import me.juangoncalves.mentra.domain.errors.*
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.elapsedMinutes
import me.juangoncalves.mentra.extensions.toPrice
import me.juangoncalves.mentra.log.Logger
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinPriceDao: CoinPriceDao,
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource,
    private val coinMapper: CoinMapper,
    private val logger: Logger
) : CoinRepository {

    override val pricesOfCoinsInUse: Flow<Map<Coin, Price>>
        get() = _pricesOfCoinsInUse

    private val _pricesOfCoinsInUse: Flow<Map<Coin, Price>> =
        coinPriceDao.getActiveCoinPrices().associateByCoin()

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
            } catch (e: Exception) {
                Either.Left(FetchPriceFailure(cacheException.latestAvailablePrice))
            }
        }
    }

    private fun Flow<List<CoinPriceModel>>.associateByCoin(): Flow<Map<Coin, Price>> =
        map { prices ->
            val coinPricesMap = hashMapOf<Coin, Price>()

            prices.forEach { priceModel ->
                val coin = localDataSource.findCoinBySymbol(priceModel.coinSymbol) ?: return@forEach
                coinPricesMap[coin] =
                    priceModel.valueInUSD.toPrice(timestamp = priceModel.timestamp)
            }

            coinPricesMap
        }

}