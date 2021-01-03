package me.juangoncalves.mentra.domain_layer.usecases.preference

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.FlowUseCase
import javax.inject.Inject

class GetTimeUnitPreferenceStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : FlowUseCase<TimeGranularity> {

    override operator fun invoke(): Flow<TimeGranularity> =
        preferenceRepository.valueChartTimeUnitStream

}