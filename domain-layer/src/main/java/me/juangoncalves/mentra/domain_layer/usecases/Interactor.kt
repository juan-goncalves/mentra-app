package me.juangoncalves.mentra.domain_layer.usecases

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure


interface Interactor<Params, Result> {
    suspend operator fun invoke(params: Params): Either<Failure, Result>
}

interface VoidInteractor<Result> : Interactor<Unit, Result> {

    suspend operator fun invoke(): Either<Failure, Result>

    override suspend fun invoke(params: Unit): Either<Failure, Result> {
        return invoke()
    }

}
