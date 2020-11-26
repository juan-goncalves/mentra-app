package me.juangoncalves.mentra.data.repositories

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.IoDispatcher
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PreferenceRepository {

    companion object Keys {
        const val ValueChartTimeGranularity = "value_chart_time_granularity"
    }

    override val valueChartTimeUnitStream: Flow<TimeGranularity>
        get() = _valueChartTimeGranularityStream

    override suspend fun updateTimeUnitPreference(value: TimeGranularity) =
        withContext(ioDispatcher) {
            sharedPreferences.edit()
                .putString(ValueChartTimeGranularity, value.name)
                .apply()

            _valueChartTimeGranularityStream.emit(value)
        }

    private val _valueChartTimeGranularityStream by lazy {
        val flow = MutableSharedFlow<TimeGranularity>(replay = 1)

        sharedPreferences.getString(ValueChartTimeGranularity, TimeGranularity.Daily.name)
            ?.asTimeGranularityOrDaily()
            ?.also { flow.tryEmit(it) }

        flow
    }

    private fun String.asTimeGranularityOrDaily(): TimeGranularity {
        return try {
            TimeGranularity.valueOf(this)
        } catch (e: IllegalArgumentException) {
            TimeGranularity.Daily
        }
    }

}