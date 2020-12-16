package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import me.juangoncalves.mentra.data_layer.sources.coin.CoinIconDataSource
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.Left
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.extensions.TAG
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.IconRepository
import javax.inject.Inject

class IconRepositoryImpl @Inject constructor(
    private val iconDataSource: CoinIconDataSource,
    private val logger: MentraLogger
) : IconRepository {

    override suspend fun getAlternativeIconFor(coin: Coin): Either<Failure, String?> {
        // TODO: We should use a ErrorHandler
        return try {
            val iconUrl = iconDataSource.getAlternativeIconFor(coin)
            Right(iconUrl)
        } catch (e: Exception) {
            logger.warning(TAG, "Can't to determine if a gradient icon is available for coin")
            Left(Failure())
        }
    }

}