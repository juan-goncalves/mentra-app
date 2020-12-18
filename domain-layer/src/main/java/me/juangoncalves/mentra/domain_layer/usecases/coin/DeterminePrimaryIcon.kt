package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.IconType
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.repositories.IconRepository
import me.juangoncalves.mentra.domain_layer.usecases.OldUseCase
import javax.inject.Inject

class DeterminePrimaryIcon @Inject constructor(
    private val coinRepository: CoinRepository,
    private val iconRepository: IconRepository
) : OldUseCase<Coin, String> {

    override suspend fun invoke(params: Coin): Either<OldFailure, String> =
        withContext(Dispatchers.Default) {
            if (params.iconType != IconType.Unknown) return@withContext Right(params.imageUrl)

            val result = iconRepository.getAlternativeIconFor(params)
            if (result.isLeft()) {
                // Load the default coin icon if we couldn't get the gradient icon url with certainty
                return@withContext Right(params.imageUrl)
            }

            val updates = when (val gradientIconUrl = result.requireRight()) {
                null -> params.copy(iconType = IconType.Regular)
                else -> params.copy(imageUrl = gradientIconUrl, iconType = IconType.Gradient)
            }

            coinRepository.updateCoin(updates)

            Right(updates.imageUrl)
        }

}