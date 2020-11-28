package me.juangoncalves.mentra.data.repositories

import either.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.db.models.CoinPriceModel
import me.juangoncalves.mentra.di.IoDispatcher
import me.juangoncalves.mentra.domain.errors.*
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.elapsedMinutes
import me.juangoncalves.mentra.log.Logger
import java.util.*
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource,
    private val coinMapper: CoinMapper,
    private val logger: Logger,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoinRepository {

    override val pricesOfCoinsInUse: Flow<Map<Coin, Price>>
        get() = _pricesOfCoinsInUse

    private val _pricesOfCoinsInUse: Flow<Map<Coin, Price>> =
        localDataSource.getActiveCoinPricesStream().associateByCoin()

    override suspend fun getCoins(): Either<Failure, List<Coin>> = withContext(ioDispatcher) {
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
            return@withContext Either.Right(coins)
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
        withContext(ioDispatcher) {
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

    override suspend fun updateCoin(coin: Coin): Either<Failure, Unit> = withContext(ioDispatcher) {
        // TODO: Handle exceptions
        localDataSource.updateCoin(coin)
        Either.Right(Unit)
    }


    private fun Flow<List<CoinPriceModel>>.associateByCoin(): Flow<Map<Coin, Price>> = debounce(500)
        .map { prices ->
            val coinPricesMap = hashMapOf<Coin, Price>()

            prices.forEach { priceModel ->
                val coin = localDataSource.findCoinBySymbol(priceModel.coinSymbol) ?: return@forEach

                coinPricesMap[coin] = Price(
                    priceModel.valueInUSD,
                    Currency.getInstance("USD"),
                    priceModel.timestamp
                )
            }

            coinPricesMap
        }

}