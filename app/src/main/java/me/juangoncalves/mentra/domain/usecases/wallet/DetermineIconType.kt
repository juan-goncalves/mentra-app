package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.IconType
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.network.CryptoIconsService
import java.util.*
import javax.inject.Inject

class DetermineIconType @Inject constructor(
    private val coinRepository: CoinRepository,
    private val iconService: CryptoIconsService
) : UseCase<Coin, String> {

    override suspend fun invoke(params: Coin): Either<Failure, String> {
        if (params.iconType != IconType.Unknown) return Right(params.imageUrl)

        val symbol = params.symbol.toLowerCase(Locale.ROOT)

        val gradientIconCheck = try {
            iconService.checkGradientIconAvailability(symbol)
        } catch (e: Exception) {
            // If we didn't manage to determine if the gradient icon is available (maybe there was no
            // internet connection), return the default image URL and try again next time
            return Right(params.imageUrl)
        }

        if (gradientIconCheck.code() == 404) {
            coinRepository.updateCoin(params.copy(iconType = IconType.Regular))
            return Right(params.imageUrl)
        } else if (gradientIconCheck.isSuccessful) {
            val updates = params.copy(
                imageUrl = gradientIconCheck.raw().request.url.toString(),
                iconType = IconType.Gradient
            )
            coinRepository.updateCoin(updates)
            return Right(updates.imageUrl)
        }

        return Right(params.imageUrl)
    }

}