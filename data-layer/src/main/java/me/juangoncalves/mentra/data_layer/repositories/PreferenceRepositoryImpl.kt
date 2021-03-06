package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.data_layer.sources.preferences.PreferenceLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.runCatching
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import java.time.Duration
import java.util.*
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val localDataSource: PreferenceLocalDataSource,
    private val errorHandler: ErrorHandler
) : PreferenceRepository {

    companion object Keys {
        const val ValueChartTimeGranularity = "value_chart_time_granularity"
        const val CurrencyCode = "currency_code"
        const val PeriodicRefresh = "periodic_refresh"
        const val OnboardingFinished = "onboarding_finished"
    }

    override val valueChartTimeUnitStream: Flow<TimeGranularity>
        get() = localDataSource.liveUpdatesFor(ValueChartTimeGranularity)
            .map { stringValue -> stringValue.toTimeGranularity() }

    override val currencyStream: Flow<Currency>
        get() = localDataSource.liveUpdatesFor(CurrencyCode)
            .map { stringValue -> stringValue.toCurrency() }

    override val periodicRefresh: Flow<Duration>
        get() = localDataSource.liveUpdatesFor(PeriodicRefresh)
            .map { stringValue -> stringValue.toDuration() }

    override suspend fun updateTimeUnitPreference(value: TimeGranularity): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.putString(ValueChartTimeGranularity, value.name)
        }

    override suspend fun updateCurrencyPreference(value: Currency): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.putString(CurrencyCode, value.currencyCode)
        }

    override suspend fun updatePeriodicRefresh(value: Duration): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.putString(PeriodicRefresh, value.toHours().toString())
        }

    override suspend fun hasFinishedOnboarding(): Either<Failure, Boolean> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.getString(OnboardingFinished) == "1"
        }

    override suspend fun updateOnboardingStatus(wasCompleted: Boolean): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            val value = if (wasCompleted) "1" else "0"
            localDataSource.putString(OnboardingFinished, value)
        }

    private fun String?.toTimeGranularity(): TimeGranularity {
        if (this == null) return TimeGranularity.Daily

        return when (this) {
            TimeGranularity.Daily.name -> TimeGranularity.Daily
            TimeGranularity.Weekly.name -> TimeGranularity.Weekly
            TimeGranularity.Monthly.name -> TimeGranularity.Monthly
            // Default value in any case
            else -> TimeGranularity.Daily
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