package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
import me.juangoncalves.mentra.core.errors.*
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.wallet_management.data.models.CoinModel
import me.juangoncalves.mentra.features.wallet_management.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

class CoinRepositoryImpl(
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource,
    private val logger: Logger
) : CoinRepository {

    companion object {
        private const val TAG = "CoinRepositoryImpl"
    }

    override suspend fun getCoins(): Either<Failure, List<Coin>> {
        val cachedCoins = try {
            localDataSource.getStoredCoins()
        } catch (e: StorageException) {
            logger.warning(TAG, "Exception while trying to get cached coins.\n$e")
            null
        }

        if (cachedCoins != null && cachedCoins.isNotEmpty()) {
            val coins = cachedCoins
                .map { it.toDomain() }
                .filterNot { it == Coin.Invalid }
            return Either.Right(coins)
        }

        return try {
            val coinSchemas = remoteDataSource.fetchCoins()
            val coins = coinSchemas
                .map { it.toDomain() }
                .filterNot { it == Coin.Invalid }
            try {
                localDataSource.storeCoins(coins)
            } catch (e: StorageException) {
                logger.warning(TAG, "Exception while trying to cache coins.\n$e")
            }
            Either.Right(coins)
        } catch (e: ServerException) {
            Either.Left(ServerFailure())
        }
    }

    override suspend fun getCoinPrice(coin: Coin, currency: Currency): Either<Failure, Price> {
        return try {
            val cachedPrice = localDataSource.getLastCoinPrice(coin, currency)
            val durationSinceFetch = Duration.between(cachedPrice.date, LocalDateTime.now())
            val minutesSinceFetch = abs(durationSinceFetch.toMinutes())
            if (minutesSinceFetch <= 5) {
                Either.Right(cachedPrice)
            } else {
                throw CacheMissException()
            }
        } catch (e: CacheMissException) {
            val price = remoteDataSource.fetchCoinPrice(coin, currency)
            localDataSource.storeCoinPrice(coin, price)
            Either.Right(price)
        }
    }

    private fun CoinModel.toDomain(): Coin {
        return Coin(
            name = name,
            symbol = symbol,
            imageUrl = imageUrl
        )
    }

    private fun CoinSchema.toDomain(): Coin {
        return Coin(
            name = name ?: return Coin.Invalid,
            symbol = symbol ?: return Coin.Invalid,
            imageUrl = imageUrl ?: return Coin.Invalid
        )
    }

}