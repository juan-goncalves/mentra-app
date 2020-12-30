package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidInteractor
import java.time.Duration
import javax.inject.Inject


class GetPeriodicRefreshPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : VoidInteractor<Duration> {

    override suspend operator fun invoke(): Either<Failure, Duration> {
        return preferenceRepository.periodicRefresh.first().toRight()
    }

}