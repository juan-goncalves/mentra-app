package me.juangoncalves.mentra.domain.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.toRight
import javax.inject.Inject

class GetTimeUnitPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<Unit, TimeGranularity> {

    override suspend fun invoke(params: Unit): Either<Failure, TimeGranularity> {
        return preferenceRepository.valueChartTimeUnitStream.first().toRight()
    }

}
