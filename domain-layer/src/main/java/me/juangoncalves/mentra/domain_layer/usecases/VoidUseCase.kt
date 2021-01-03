package me.juangoncalves.mentra.domain_layer.usecases

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure

interface VoidUseCase<Result> : UseCase<Unit, Result> {

    suspend operator fun invoke(): Either<Failure, Result>

    override suspend fun invoke(params: Unit): Either<Failure, Result> {
        return invoke()
    }

}
