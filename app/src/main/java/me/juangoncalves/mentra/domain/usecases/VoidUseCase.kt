package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure

interface VoidUseCase<T> : UseCase<Unit, T> {

    suspend operator fun invoke(): Either<Failure, T>

    override suspend fun invoke(params: Unit): Either<Failure, T> {
        return invoke()
    }

}
