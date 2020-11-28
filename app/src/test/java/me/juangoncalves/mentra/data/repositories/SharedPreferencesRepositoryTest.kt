package me.juangoncalves.mentra.data.repositories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.MainCoroutineRule
import me.juangoncalves.mentra.data.repositories.SharedPreferencesRepository.Keys.ValueChartTimeGranularity
import me.juangoncalves.mentra.domain.models.TimeGranularity
import me.juangoncalves.mentra.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class SharedPreferencesRepositoryTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    //endregion

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sut: SharedPreferencesRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context).apply {
            edit().clear().commit()
        }
        sut = SharedPreferencesRepository(sharedPrefs, mainCoroutineRule.dispatcher)
    }

    @Test
    fun `updateTimeUnitPreference saves the received value into the appropriate key`() =
        runBlockingTest {
            // Arrange
            // Act
            sut.updateTimeUnitPreference(TimeGranularity.Monthly)

            // Assert
            val stored = sharedPrefs.getString(ValueChartTimeGranularity, "NONE")
            stored shouldBe "Monthly"
        }

    @Test
    fun `valueChartTimeUnitStream emits the current TimeUnit preference when initialized`() =
        runBlockingTest {
            // Arrange
            sharedPrefs.edit { putString(ValueChartTimeGranularity, "Monthly") }

            // Act
            val result = sut.valueChartTimeUnitStream.first()

            // Assert
            result shouldBe TimeGranularity.Monthly
        }

    @Test
    fun `valueChartTimeUnitStream emits the TimeUnit preference updates`() =
        runBlockingTest {
            // Arrange
            sharedPrefs.edit { putString(ValueChartTimeGranularity, "Monthly") }

            // Act
            val results = mutableListOf<TimeGranularity>()

            sut.valueChartTimeUnitStream.take(2)
                .onEach { results.add(it) }
                .launchIn(this)

            sut.updateTimeUnitPreference(TimeGranularity.Daily)

            // Assert
            results[0] shouldBe TimeGranularity.Monthly
            results[1] shouldBe TimeGranularity.Daily
        }

    @Test
    fun `valueChartTimeUnitStream returns the daily time unit if the stored value is not valid`() =
        runBlockingTest {
            // Arrange
            sharedPrefs.edit { putString(ValueChartTimeGranularity, "invalid time unit") }

            // Act
            val result = sut.valueChartTimeUnitStream.first()

            // Assert
            result shouldBe TimeGranularity.Daily
        }

    //region Helpers
    //endregion

}