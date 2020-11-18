package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.IconType
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.IconRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.requireRight
import javax.inject.Inject

class DeterminePrimaryIcon @Inject constructor(
    private val coinRepository: CoinRepository,
    private val iconRepository: IconRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : UseCase<Coin, String> {

    override suspend fun invoke(params: Coin): Either<Failure, String> =
        withContext(defaultDispatcher) {
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