package me.juangoncalves.mentra.features.stats.mapper

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.MainCoroutineRule
import me.juangoncalves.mentra.Ripple
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PiePortionMapperTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    //endregion

    private lateinit var sut: PiePortionMapper

    @Before
    fun setUp() {
        sut = PiePortionMapper(mainCoroutineRule.dispatcher)
    }

    @Test
    fun `map converts the coin-value map into an appropriate array of pie portions`() =
        runBlockingTest {
            // Arrange
            val data = mapOf(
                Bitcoin to 0.3,
                Ethereum to 0.1,
                Ripple to 0.6
            )

            // Act
            val result = sut.map(data)
            val btcPortion = result.find { it.text == Bitcoin.symbol }
            val ethPortion = result.find { it.text == Ethereum.symbol }
            val xrpPortion = result.find { it.text == Ripple.symbol }

            // Assert
            assertEquals(3, result.size)
            assertNotNull(btcPortion)
            assertNotNull(ethPortion)
            assertNotNull(xrpPortion)
            assertThat(btcPortion!!.percentage, closeTo(0.3, 0.001))
            assertThat(ethPortion!!.percentage, closeTo(0.1, 0.001))
            assertThat(xrpPortion!!.percentage, closeTo(0.6, 0.001))
        }

    //region Helpers
    //endregion

}