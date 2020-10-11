package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure

interface UseCase<Params, Result> {
    suspend operator fun invoke(params: Params): Either<Failure, Result>
}
