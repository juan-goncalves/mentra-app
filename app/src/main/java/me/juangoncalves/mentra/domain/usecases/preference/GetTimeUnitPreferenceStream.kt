package me.juangoncalves.mentra.domain.usecases.preference

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import javax.inject.Inject

class GetTimeUnitPreferenceStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    operator fun invoke(): Flow<TimeGranularity> = preferenceRepository.valueChartTimeUnitStream

}