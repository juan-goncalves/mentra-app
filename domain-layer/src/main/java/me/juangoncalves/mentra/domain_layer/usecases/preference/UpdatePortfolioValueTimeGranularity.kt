package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class UpdatePortfolioValueTimeGranularity @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<TimeGranularity, Unit> {

    override suspend fun invoke(params: TimeGranularity): Either<Failure, Unit> {
        return preferenceRepository.updateTimeUnitPreference(params)
    }

}