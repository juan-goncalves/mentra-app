package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidOldUseCase
import java.time.Duration
import javax.inject.Inject


class GetPeriodicRefreshPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : VoidOldUseCase<Duration> {

    override suspend operator fun invoke(): Either<OldFailure, Duration> {
        return preferenceRepository.periodicRefresh.first().toRight()
    }

}