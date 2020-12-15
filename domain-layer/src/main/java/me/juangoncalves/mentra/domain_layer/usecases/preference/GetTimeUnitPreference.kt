package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class GetTimeUnitPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<Unit, TimeGranularity> {

    override suspend fun invoke(params: Unit): Either<Failure, TimeGranularity> {
        return preferenceRepository.valueChartTimeUnitStream.first().toRight()
    }

}
