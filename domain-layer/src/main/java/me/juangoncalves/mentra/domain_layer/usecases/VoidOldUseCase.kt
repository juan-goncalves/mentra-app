package me.juangoncalves.mentra.domain_layer.usecases

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure

interface VoidOldUseCase<T> : OldUseCase<Unit, T> {

    suspend operator fun invoke(): Either<OldFailure, T>

    override suspend fun invoke(params: Unit): Either<OldFailure, T> {
        return invoke()
    }

}
