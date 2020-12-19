package me.juangoncalves.mentra.data_layer.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.data_layer.toPrice
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import org.junit.Before
import org.junit.Test

class PortfolioRepositoryImplTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var localDsMock: PortfolioLocalDataSource
    @MockK lateinit var errorHandlerMock: ErrorHandler
    //endregion

    private lateinit var sut: PortfolioRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = PortfolioRepositoryImpl(localDsMock, errorHandlerMock)
        every { errorHandlerMock.getFailure(any()) } returns Failure.Unknown
    }

    @Test
    fun `updatePortfolioValue uses the local data source to commit the changes`() = runBlocking {
        // Arrange
        val newValue = 123.0.toPrice()
        coEvery { localDsMock.insertValue(any()) } just Runs

        // Act
        val result = sut.updatePortfolioValue(newValue)

        // Assert
        coVerify { localDsMock.insertValue(newValue) }
        result.rightValue shouldBe Unit
    }

    @Test
    fun `updatePortfolioValue returns a Failure when the local data source update fails`() =
        runBlocking {
            // Arrange
            val newValue = 123.0.toPrice()
            coEvery { localDsMock.insertValue(any()) } throws RuntimeException()

            // Act
            val result = sut.updatePortfolioValue(newValue)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    //region Helpers
    //endregion

}