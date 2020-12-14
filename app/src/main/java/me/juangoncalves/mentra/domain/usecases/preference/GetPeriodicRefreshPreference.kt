package me.juangoncalves.mentra.domain.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain.usecases.VoidUseCase
import me.juangoncalves.mentra.extensions.toRight
import java.time.Duration
import javax.inject.Inject

class GetPeriodicRefreshPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : VoidUseCase<Duration> {

    override suspend operator fun invoke(): Either<Failure, Duration> {
        return preferenceRepository.periodicRefresh.first().toRight()
    }

}