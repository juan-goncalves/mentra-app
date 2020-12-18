package me.juangoncalves.mentra.domain_layer.usecases

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure

interface OldUseCase<Params, Result> {
    suspend operator fun invoke(params: Params): Either<OldFailure, Result>
}
