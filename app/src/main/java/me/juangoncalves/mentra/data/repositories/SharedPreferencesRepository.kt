package me.juangoncalves.mentra.data.repositories

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.extensions.TAG
import java.time.Duration
import java.util.*
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferenceRepository {

    companion object Keys {
        const val ValueChartTimeGranularity = "value_chart_time_granularity"
        const val CurrencyCode = "currency_code"
        const val PeriodicRefresh = "periodic_refresh"
    }

    private val sharedPreferencesListener = OnSharedPreferenceChangeListener { prefs, key ->
        Log.i(TAG, "Updated preference \"$key\"")
        val value = prefs.getString(key, null)
        when (key) {
            ValueChartTimeGranularity -> _valueChartTimeGranularityStream.tryEmit(value.toTimeGranularity())
            CurrencyCode -> _currencyStream.tryEmit(value.toCurrency())
            PeriodicRefresh -> _periodicRefreshStream.tryEmit(value.toDuration())
        }
    }

    private val _valueChartTimeGranularityStream: MutableSharedFlow<TimeGranularity> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    private val _currencyStream: MutableSharedFlow<Currency> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    private val _periodicRefreshStream: MutableSharedFlow<Duration> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        sharedPreferences.getString(ValueChartTimeGranularity, TimeGranularity.Daily.name)
            .toTimeGranularity()
            .also { _valueChartTimeGranularityStream.tryEmit(it) }

        sharedPreferences.getString(CurrencyCode, "USD")
            .toCurrency()
            .also { _currencyStream.tryEmit(it) }

        sharedPreferences.getString(PeriodicRefresh, "12")
            .toDuration()
            .also { _periodicRefreshStream.tryEmit(it) }
    }

    override val valueChartTimeUnitStream: Flow<TimeGranularity>
        get() = _valueChartTimeGranularityStream

    override val currencyStream: Flow<Currency>
        get() = _currencyStream

    override val periodicRefresh: Flow<Duration>
        get() = _periodicRefreshStream

    override suspend fun updateTimeUnitPreference(value: TimeGranularity) =
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(ValueChartTimeGranularity, value.name)
                .apply()
        }

    override suspend fun updateCurrencyPreference(value: Currency) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(CurrencyCode, value.currencyCode)
            .apply()
    }

    override suspend fun updatePeriodicRefresh(value: Duration) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(PeriodicRefresh, value.toHours().toString())
            .apply()
    }

    private fun String?.toTimeGranularity(): TimeGranularity {
        if (this == null) return TimeGranularity.Daily

        return try {
            TimeGranularity.valueOf(this)
        } catch (e: IllegalArgumentException) {
            TimeGranularity.Daily
        }
    }

    private fun String?.toCurrency(): Currency {
        if (this == null) return Currency.getInstance("USD")

        return try {
            Currency.getInstance(this)
        } catch (e: Exception) {
            Currency.getInstance("USD")
        }
    }

    private fun String?.toDuration(): Duration {
        return when (this?.toIntOrNull()) {
            null, 12 -> Duration.ofHours(12)
            3 -> Duration.ofHours(3)
            6 -> Duration.ofHours(6)
            24 -> Duration.ofDays(1)
            else -> error("Unsupported refresh duration: $this")
        }
    }

}

