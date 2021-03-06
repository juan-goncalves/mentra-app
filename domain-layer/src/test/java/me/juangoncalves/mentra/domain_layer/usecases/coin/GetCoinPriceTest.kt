package me.juangoncalves.mentra.domain_layer.usecases.coin

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.domain_layer.Bitcoin
import me.juangoncalves.mentra.domain_layer.Ethereum
import me.juangoncalves.mentra.domain_layer.USD
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.toPrice
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Test

class GetCoinPriceTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var coinRepositoryMock: CoinRepository
    //endregion

    private lateinit var sut: GetCoinPrice

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = GetCoinPrice(coinRepositoryMock)
    }

    @Test
    fun `should return the price of the selected coin from the repository`() = runBlocking {
        // Arrange
        val fakeResult = Right(9834.23.toPrice())
        coEvery { coinRepositoryMock.getCoinPrice(Bitcoin) } returns fakeResult

        // Act
        val result = sut(Bitcoin)

        // Assert
        with(result.requireRight()) {
            value shouldBeCloseTo 9834.23.toBigDecimal()
            currency shouldBe USD
        }
    }

    @Test
    fun `should return a Failure if the price of the selected coin is not found`() = runBlocking {
        // Arrange
        coEvery { coinRepositoryMock.getCoinPrice(Ethereum) } returns Failure.Unknown.toLeft()

        // Act
        val result = sut(Ethereum)

        // Assert
        result.leftValue shouldBeA Failure::class
    }

    //region Helpers
    //endregion

}