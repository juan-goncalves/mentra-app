package me.juangoncalves.mentra.platform

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain_layer.models.Coin
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class CoinIconResourceDataSourceTest {

    private lateinit var sut: CoinIconResourceDataSource

    @Before
    fun setUp() {
        sut = CoinIconResourceDataSource(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `getAlternativeIconFor returns the URI to the corresponding icon resource when it exists`() =
        runBlocking {
            // Arrange
            // Act
            val result = sut.getAlternativeIconFor(Bitcoin)

            // Assert
            assertEquals(
                "android.resource://me.juangoncalves.mentra.debug/raw/${R.raw.btc}",
                result
            )
        }

    @Test
    fun `getAlternativeIconFor returns null when there's no corresponding icon resource for the coin`() =
        runBlocking {
            // Arrange
            val coin = Coin(
                name = "Unit Test Coin",
                symbol = "UTC",
                imageUrl = "https://juangoncalves.me/utcc.svg",
            )

            // Act
            val result = sut.getAlternativeIconFor(coin)

            // Assert
            assertEquals(null, result)
        }

}