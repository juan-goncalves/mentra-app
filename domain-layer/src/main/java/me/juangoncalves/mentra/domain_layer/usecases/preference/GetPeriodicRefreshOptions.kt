package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.usecases.VoidUseCase
import java.time.Duration
import javax.inject.Inject

class GetPeriodicRefreshOptions @Inject constructor() : VoidUseCase<List<Duration>> {

    override suspend fun invoke(): Either<Failure, List<Duration>> = listOf(
        Duration.ofDays(1),
        Duration.ofHours(12),
        Duration.ofHours(6),
        Duration.ofHours(3)
    ).toRight()

}