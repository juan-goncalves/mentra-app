package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.OldUseCase
import javax.inject.Inject

class GetTimeUnitPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : OldUseCase<Unit, TimeGranularity> {

    override suspend fun invoke(params: Unit): Either<OldFailure, TimeGranularity> {
        return preferenceRepository.valueChartTimeUnitStream.first().toRight()
    }

}
