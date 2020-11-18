package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.InternetConnectionFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.repositories.IconRepository
import me.juangoncalves.mentra.extensions.Left
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.log.Logger
import me.juangoncalves.mentra.network.CryptoIconsService
import java.util.*
import javax.inject.Inject

class IconRepositoryImpl @Inject constructor(
    private val iconService: CryptoIconsService,
    private val logger: Logger
) : IconRepository {

    override suspend fun getAlternativeIconFor(coin: Coin): Either<Failure, String?> {
        val symbol = coin.symbol.toLowerCase(Locale.ROOT)

        val gradientIconCheck = try {
            iconService.checkGradientIconAvailability(symbol)
        } catch (e: Exception) {
            // If we didn't manage to determine if the gradient icon is available (maybe there was no
            // internet connection), return the default image URL and try again next time
            logger.warning(TAG, "Can't to determine if a gradient icon is available for coin")
            return Left(InternetConnectionFailure())
        }

        return if (gradientIconCheck.isSuccessful) {
            Right(gradientIconCheck.raw().request.url.toString())
        } else {
            Right(null)
        }
    }

}