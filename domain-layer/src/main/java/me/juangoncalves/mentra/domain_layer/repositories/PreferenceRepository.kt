package me.juangoncalves.mentra.domain_layer.repositories

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import java.time.Duration
import java.util.*

/** Every operation must be infallible, returning a default value if anything goe wrong. */
interface PreferenceRepository {

    val valueChartTimeUnitStream: Flow<TimeGranularity>
    val currencyStream: Flow<Currency>
    val periodicRefresh: Flow<Duration>

    suspend fun updateTimeUnitPreference(value: TimeGranularity)

    suspend fun updateCurrencyPreference(value: Currency)

    suspend fun updatePeriodicRefresh(value: Duration)

}