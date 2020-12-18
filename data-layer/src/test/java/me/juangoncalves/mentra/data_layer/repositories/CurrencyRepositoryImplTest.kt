package me.juangoncalves.mentra.data_layer.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.data_layer.CAD
import me.juangoncalves.mentra.data_layer.EUR
import me.juangoncalves.mentra.data_layer.USD
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import org.junit.Before
import org.junit.Test

class CurrencyRepositoryImplTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var localDsMock: CurrencyLocalDataSource
    @MockK lateinit var remoteDsMock: CurrencyRemoteDataSource
    @MockK lateinit var errorHandlerMock: ErrorHandler
    //endregion

    private lateinit var sut: CurrencyRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = CurrencyRepositoryImpl(remoteDsMock, localDsMock, errorHandlerMock)
        every { errorHandlerMock.getFailure(any()) } returns Failure.Unknown
    }

    @Test
    fun `getCurrencies returns the cached coins if they exist`() = runBlocking {
        // Arrange
        val cachedCurrencies = setOf(USD, EUR, CAD)
        coEvery { localDsMock.getCurrencies() } returns cachedCurrencies

        // Act
        val result = sut.getCurrencies()

        // Assert
        result.rightValue shouldBe cachedCurrencies
        coVerify { localDsMock.getCurrencies() }
        verify { remoteDsMock wasNot Called }
    }

    @Test
    fun `getCurrencies fetches and caches the currencies from the network when the cache is empty`() =
        runBlocking {
            // Arrange
            val remoteCurrencies = setOf(USD, EUR, CAD)
            coEvery { remoteDsMock.fetchCurrencies() } returns remoteCurrencies
            coEvery { localDsMock.getCurrencies() } returns emptySet()

            // Act
            val result = sut.getCurrencies()

            // Assert
            result.rightValue shouldBe remoteCurrencies
            coVerify { remoteDsMock.fetchCurrencies() }
            coVerify { localDsMock.saveCurrencies(remoteCurrencies.toList()) }
        }

    @Test
    fun `getCurrencies fetches the list of currencies from the network if querying the cache fails`() =
        runBlocking {
            // Arrange
            val remoteCurrencies = setOf(USD, EUR, CAD)
            coEvery { localDsMock.getCurrencies() } throws RuntimeException()
            coEvery { remoteDsMock.fetchCurrencies() } returns remoteCurrencies

            // Act
            val result = sut.getCurrencies()

            // Assert
            result.rightValue shouldBe remoteCurrencies
        }

    @Test
    fun `getCurrencies fetches the list of currencies from the network and returns them even if the caching fails`() =
        runBlocking {
            // Arrange
            val remoteCurrencies = setOf(USD, EUR, CAD)
            coEvery { remoteDsMock.fetchCurrencies() } returns remoteCurrencies
            coEvery { localDsMock.getCurrencies() } returns emptySet()
            coEvery { localDsMock.saveCurrencies(any()) } throws RuntimeException()

            // Act
            val result = sut.getCurrencies()

            // Assert
            result.rightValue shouldBe remoteCurrencies
        }

    @Test
    fun `getCurrencies returns a Failure when the cache is empty and the currency list fetch fails`() =
        runBlocking {
            // Arrange
            val exception = RuntimeException()
            coEvery { remoteDsMock.fetchCurrencies() } throws exception
            coEvery { localDsMock.getCurrencies() } returns emptySet()

            // Act
            val result = sut.getCurrencies()

            // Assert
            result.leftValue shouldBeA Failure::class
            coVerify { errorHandlerMock.getFailure(exception) }
        }

    //region Helpers
    //endregion

}