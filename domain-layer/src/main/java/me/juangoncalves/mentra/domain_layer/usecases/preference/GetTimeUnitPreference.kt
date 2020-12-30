package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidInteractor
import javax.inject.Inject

class GetTimeUnitPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : VoidInteractor<TimeGranularity> {

    override suspend fun invoke(): Either<Failure, TimeGranularity> {
        return preferenceRepository.valueChartTimeUnitStream.first().toRight()
    }

}
