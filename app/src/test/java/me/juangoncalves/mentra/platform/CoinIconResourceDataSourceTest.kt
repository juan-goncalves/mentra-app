package me.juangoncalves.mentra.platform

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.test_utils.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class CoinIconResourceDataSourceTest {

    private lateinit var context: Context
    private lateinit var sut: CoinIconResourceDataSource

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sut = CoinIconResourceDataSource(context)
    }

    @Test
    fun `getAlternativeIconFor returns the URI to the corresponding icon resource when it exists`() =
        runBlocking {
            // Arrange
            // Act
            val result = sut.getAlternativeIconFor(Bitcoin)

            // Assert
            val expected = "android.resource://${context.packageName}/raw/${R.raw.btc}"

            result shouldBe expected
        }

    @Test
    fun `getAlternativeIconFor returns null when there's no corresponding icon resource for the coin`() =
        runBlocking {
            // Arrange
            val coin = Coin(
                name = "Unit Test Coin",
                symbol = "UTC",
                imageUrl = "https://juangoncalves.me/utcc.svg",
                position = 1,
            )

            // Act
            val result = sut.getAlternativeIconFor(coin)

            // Assert
            result shouldBe null
        }

}