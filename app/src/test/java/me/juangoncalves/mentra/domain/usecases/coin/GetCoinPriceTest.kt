package me.juangoncalves.mentra.domain.usecases.coin

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.PriceNotFound
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.requireRight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCoinPriceTest {

    @MockK lateinit var coinRepositoryMock: CoinRepository

    private lateinit var getCoinPrice: GetCoinPrice

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        getCoinPrice = GetCoinPrice(coinRepositoryMock)
    }

    @Test
    fun `should return the price of the selected coin from the repository`() = runBlocking {
        // Arrange
        val fakeResult = Right(9834.23.toPrice())
        coEvery { coinRepositoryMock.getCoinPrice(Bitcoin) } returns fakeResult

        // Act
        val result = getCoinPrice(Bitcoin)

        // Assert
        with(result.requireRight()) {
            value shouldBeCloseTo 9834.23.toBigDecimal()
            currency shouldBe USD
        }
    }

    @Test
    fun `should fail if the price of the selected coin is not found`() = runBlocking {
        // Arrange
        val failure = PriceNotFound(Ethereum)
        coEvery { coinRepositoryMock.getCoinPrice(Ethereum) } returns Left(failure)

        // Act
        val result = getCoinPrice(Ethereum)

        // Assert
        assertTrue(result.requireLeft() is PriceNotFound)
        assertEquals((result.requireLeft() as PriceNotFound).coin, Ethereum)
    }

}