package me.juangoncalves.mentra.data_layer.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import me.juangoncalves.mentra.data_layer.CAD
import me.juangoncalves.mentra.data_layer.EUR
import me.juangoncalves.mentra.data_layer.USD
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.data_layer.toPrice
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
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

    @Test
    fun `exchange converts the price using the cached rate if it exists and is not expired`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)
            coEvery { localDsMock.getExchangeRate(USD, EUR) } returns 1.2.toPrice(currency = EUR)

            // Act
            val result = sut.exchange(original, EUR)

            // Assert
            coVerify { remoteDsMock.fetchExchangeRates(any()) wasNot Called }
            with(result.requireRight() ?: Price.None) {
                value shouldBeCloseTo 12.24
                currency shouldBe EUR
            }
        }

    @Test
    fun `exchange fetches the rate from the network and caches it if it's not cached`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)
            coEvery { localDsMock.getExchangeRate(USD, EUR) } returns null
            coEvery { remoteDsMock.fetchExchangeRates(USD) } returns mapOf(EUR to 1.2.toBigDecimal())

            // Act
            val result = sut.exchange(original, EUR)

            // Assert
            coVerify { localDsMock.saveExchangeRates(USD, any()) }
            with(result.requireRight() ?: Price.None) {
                value shouldBeCloseTo 12.24
                currency shouldBe EUR
            }
        }

    @Test
    fun `exchange fetches the rate from the network and caches it if the cached value expired`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)
            val expiredRate = 1.2.toPrice(
                currency = EUR,
                timestamp = Clock.System.now()
                    .minus(9, DAY, currentSystemDefault())
                    .toLocalDateTime(currentSystemDefault())
            )
            coEvery { localDsMock.getExchangeRate(USD, EUR) } returns expiredRate
            coEvery { remoteDsMock.fetchExchangeRates(USD) } returns mapOf(EUR to 1.1.toBigDecimal())

            // Act
            val result = sut.exchange(original, EUR)

            // Assert
            coVerify { localDsMock.saveExchangeRates(USD, any()) }
            with(result.requireRight() ?: Price.None) {
                value shouldBeCloseTo 11.22
                currency shouldBe EUR
            }
        }

    @Test
    fun `exchange returns a Failure if there's no cached value and the fetch fails`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)
            coEvery { localDsMock.getExchangeRate(USD, EUR) } throws RuntimeException()
            coEvery { remoteDsMock.fetchExchangeRates(USD) } throws RuntimeException()

            // Act
            val result = sut.exchange(original, EUR)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    @Test
    fun `exchange converts and returns the price using the network rate even if the caching fails`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)
            coEvery { localDsMock.getExchangeRate(USD, EUR) } returns null
            coEvery { localDsMock.saveExchangeRates(any(), any()) } throws RuntimeException()
            coEvery { remoteDsMock.fetchExchangeRates(USD) } returns mapOf(EUR to 1.1.toBigDecimal())

            // Act
            val result = sut.exchange(original, EUR)

            // Assert
            with(result.requireRight() ?: Price.None) {
                value shouldBeCloseTo 11.22
                currency shouldBe EUR
            }
        }

    @Test
    fun `exchange returns null if the rate is not available in both the cache and network`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)
            coEvery { localDsMock.getExchangeRate(USD, EUR) } returns null
            coEvery { remoteDsMock.fetchExchangeRates(USD) } returns mapOf()

            // Act
            val result = sut.exchange(original, EUR)

            // Assert
            result.requireRight() shouldBe null
        }

    @Test
    fun `exchange returns the received money if it already uses the target currency without hitting the cache or network`() =
        runBlocking {
            // Arrange
            val original = 10.2.toPrice(currency = USD)

            // Act
            val result = sut.exchange(original, USD)

            // Assert
            with(result.requireRight() ?: Price.None) {
                value shouldBeCloseTo 10.2
                currency shouldBe USD
            }

            verify { localDsMock wasNot Called }
            verify { remoteDsMock wasNot Called }
        }

    //region Helpers
    //endregion

}