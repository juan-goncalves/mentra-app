package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidUseCase
import javax.inject.Inject


class RefreshSupportedCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : VoidUseCase<Unit> {

    override suspend fun invoke(): Either<OldFailure, Unit> {
        TODO()
//        val fetchOperation = coinRepository.getCoins(forceNonCached = true)
//        return if (fetchOperation.isLeft()) {
//            fetchOperation.requireLeft().toLeft()
//        } else {
//            Right(Unit)
//        }
    }

}