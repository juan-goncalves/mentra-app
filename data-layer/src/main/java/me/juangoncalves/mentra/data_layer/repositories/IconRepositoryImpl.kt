package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import me.juangoncalves.mentra.data_layer.sources.coin.CoinIconDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.runCatching
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.IconRepository
import javax.inject.Inject

class IconRepositoryImpl @Inject constructor(
    private val iconDataSource: CoinIconDataSource,
    private val errorHandler: ErrorHandler
) : IconRepository {

    override suspend fun getAlternativeIconFor(coin: Coin): Either<Failure, String?> =
        errorHandler.runCatching(Dispatchers.IO) {
            iconDataSource.getAlternativeIconFor(coin)
        }

}