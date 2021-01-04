package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import java.time.Duration
import javax.inject.Inject

class UpdatePeriodicRefreshPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<Duration, Unit> {

    override suspend fun invoke(params: Duration): Either<Failure, Unit> {
        return preferenceRepository.updatePeriodicRefresh(params)
    }

}