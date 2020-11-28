package me.juangoncalves.mentra.features.stats.mapper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PiePortionMapperTest {

    //region Rules
    //endregion

    //region Mocks
    //endregion

    private lateinit var sut: PiePortionMapper

    @Before
    fun setUp() {
        sut = PiePortionMapper()
    }

    @Test
    fun `map converts the coin-value map into an appropriate array of pie portions`() =
        runBlocking {
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
            result.size shouldBe 3
            btcPortion!!.percentage shouldBeCloseTo 0.3
            ethPortion!!.percentage shouldBeCloseTo 0.1
            xrpPortion!!.percentage shouldBeCloseTo 0.6
        }

    //region Helpers
    //endregion

}