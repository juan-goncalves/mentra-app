package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireLeft
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidInteractor
import javax.inject.Inject


class RefreshSupportedCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : VoidInteractor<Unit> {

    override suspend fun invoke(): Either<Failure, Unit> {
        val fetchOperation = coinRepository.getCoins(forceNonCached = true)
        return if (fetchOperation.isLeft()) {
            fetchOperation.requireLeft().toLeft()
        } else {
            Right(Unit)
        }
    }

}