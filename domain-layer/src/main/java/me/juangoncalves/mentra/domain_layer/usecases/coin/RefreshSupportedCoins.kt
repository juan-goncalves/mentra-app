package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import either.fold
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidInteractor
import javax.inject.Inject


class RefreshSupportedCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : VoidInteractor<Unit> {

    override suspend fun invoke(): Either<Failure, Unit> {
        return coinRepository.getCoins(forceNonCached = true).fold(
            left = { failure -> failure.toLeft() },
            right = { Unit.toRight() }
        )
    }

}