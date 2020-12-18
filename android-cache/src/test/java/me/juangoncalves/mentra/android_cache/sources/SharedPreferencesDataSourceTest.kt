package me.juangoncalves.mentra.android_cache.sources

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.test_utils.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class, sdk = [28])
class SharedPreferencesDataSourceTest {

    //region Rules
    //endregion

    //region Mocks
    //endregion

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sut: SharedPreferencesDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit { clear() }
        sut = SharedPreferencesDataSource(sharedPrefs)
    }

    @Test
    fun `putString stores the key value pair in SharedPreferences`() = runBlocking {
        // Arrange
        val key = "example_key"
        val value = "example_value"

        // Act
        sut.putString(key, value)

        // Assert
        val storedValue = sharedPrefs.getString(key, null)
        storedValue shouldBe value
    }

    @Test
    fun `putString clears the value from SharedPreferences if its null`() = runBlocking {
        // Arrange
        val key = "example_key"
        sharedPrefs.edit {
            putString(key, "Some non-null value")
        }

        // Act
        sut.putString(key, null)

        // Assert
        sharedPrefs.contains(key) shouldBe false
    }

    @Test
    fun `getString returns the value stored in SharedPreferences`() = runBlocking {
        // Arrange
        val key = "test_key"
        sharedPrefs.edit {
            putString(key, "Test")
        }

        // Act
        val result = sut.getString(key)

        // Assert
        result shouldBe "Test"
    }

    @Test
    fun `getString returns null if there's no value for the key`() = runBlocking {
        // Arrange
        val key = "test_key"

        // Act
        val result = sut.getString(key)

        // Assert
        result shouldBe null
    }

    //region Helpers
    //endregion

}