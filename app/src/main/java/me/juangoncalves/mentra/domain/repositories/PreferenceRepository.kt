package me.juangoncalves.mentra.domain.repositories

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.models.TimeGranularity
import java.util.*

/** Every operation must be infallible, returning a default value if anything goe wrong. */
interface PreferenceRepository {

    val valueChartTimeUnitStream: Flow<TimeGranularity>
    val currencyStream: Flow<Currency>

    suspend fun updateTimeUnitPreference(value: TimeGranularity)

}