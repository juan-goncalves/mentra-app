package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.data.models.CoinModel
import me.juangoncalves.mentra.features.wallet_management.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Money
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository

class CoinRepositoryImpl(
    private val remoteDataSource: CoinRemoteDataSource,
    private val localDataSource: CoinLocalDataSource
) : CoinRepository {

    override suspend fun getCoins(): Either<Failure, List<Coin>> {
        val cachedCoins = localDataSource.getStoredCoins()
        if (cachedCoins.isNotEmpty()) {
            val coins = cachedCoins
                .map { it.toDomain() }
                .filterNot { it == Coin.Invalid }
            return Either.Right(coins)
        }

        val coinSchemas = remoteDataSource.fetchCoins()
        val coins = coinSchemas
            .map { it.toDomain() }
            .filterNot { it == Coin.Invalid }
        localDataSource.storeCoins(coins)
        return Either.Right(coins)
    }

    override suspend fun getCoinPrice(coin: Coin, currency: Currency): Either<Failure, Money> {
        TODO("not implemented")
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