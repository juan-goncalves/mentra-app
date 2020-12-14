package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.usecases.VoidUseCase
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.toLeft
import javax.inject.Inject

class RefreshSupportedCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : VoidUseCase<Unit> {

    override suspend fun invoke(): Either<Failure, Unit> {
        val fetchOperation = coinRepository.getCoins(forceNonCached = true)
        return if (fetchOperation.isLeft()) {
            fetchOperation.requireLeft().toLeft()
        } else {
            Right(Unit)
        }
    }

}