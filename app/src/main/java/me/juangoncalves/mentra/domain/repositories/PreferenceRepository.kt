package me.juangoncalves.mentra.domain.repositories

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.models.TimeGranularity

interface PreferenceRepository {

    val valueChartTimeUnitStream: Flow<TimeGranularity>

    suspend fun updateTimeUnitPreference(value: TimeGranularity)

}