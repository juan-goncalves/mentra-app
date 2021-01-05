package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import java.time.Duration
import java.util.*

interface PreferenceRepository {

    val valueChartTimeUnitStream: Flow<TimeGranularity>
    val currencyStream: Flow<Currency>
    val periodicRefresh: Flow<Duration>

    suspend fun updateTimeUnitPreference(value: TimeGranularity): Either<Failure, Unit>

    suspend fun updateCurrencyPreference(value: Currency): Either<Failure, Unit>

    suspend fun updatePeriodicRefresh(value: Duration): Either<Failure, Unit>

    suspend fun hasFinishedOnboarding(): Either<Failure, Boolean>

    suspend fun updateOnboardingStatus(wasCompleted: Boolean): Either<Failure, Unit>

}