package me.juangoncalves.mentra.domain.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.toRight
import javax.inject.Inject

class UpdatePortfolioValueTimeGranularity @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<TimeGranularity, Unit> {

    override suspend fun invoke(params: TimeGranularity): Either<Failure, Unit> {
        return preferenceRepository.updateTimeUnitPreference(params).toRight()
    }

}