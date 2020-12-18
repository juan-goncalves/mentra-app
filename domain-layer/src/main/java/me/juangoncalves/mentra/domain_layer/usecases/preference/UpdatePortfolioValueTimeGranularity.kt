package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class UpdatePortfolioValueTimeGranularity @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<TimeGranularity, Unit> {

    override suspend fun invoke(params: TimeGranularity): Either<OldFailure, Unit> {
        return preferenceRepository.updateTimeUnitPreference(params).toRight()
    }

}