package me.juangoncalves.mentra.domain.usecases.coin

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.PriceNotFound
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.requireRight
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
    fun `should fail if the price of the selected coin is not found`() = runBlocking {
        // Arrange
        val failure = PriceNotFound(Ethereum)
        coEvery { coinRepositoryMock.getCoinPrice(Ethereum) } returns Left(failure)

        // Act
        val result = sut(Ethereum)

        // Assert
        result.leftValue shouldBeA PriceNotFound::class
        (result.requireLeft() as PriceNotFound).coin shouldBe Ethereum
    }

    //region Helpers
    //endregion

}