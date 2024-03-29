package me.juangoncalves.mentra.data_layer.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.HOUR
import kotlinx.datetime.DateTimeUnit.Companion.MINUTE
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import me.juangoncalves.mentra.data_layer.Bitcoin
import me.juangoncalves.mentra.data_layer.Ethereum
import me.juangoncalves.mentra.data_layer.Ripple
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.data_layer.toPrice
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.requireLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CoinRepositoryImplTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var localSourceMock: CoinLocalDataSource
    @MockK lateinit var remoteSourceMock: CoinRemoteDataSource
    @MockK lateinit var errorHandlerMock: ErrorHandler
    //endregion

    private lateinit var sut: CoinRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = CoinRepositoryImpl(remoteSourceMock, localSourceMock, errorHandlerMock)
        every { errorHandlerMock.getFailure(any()) } returns Failure.Unknown
    }

    @Test
    fun `getCoins fetches and caches the coins from the network when the cache is empty`() =
        runBlocking {
            // Arrange
            val coins = listOf(Bitcoin, Ethereum, Ripple)
            coEvery { remoteSourceMock.fetchCoins() } returns coins
            coEvery { localSourceMock.getStoredCoins() } returns emptyList()

            // Act
            val result = sut.getCoins()

            // Assert
            result.rightValue shouldBe coins
            coVerify { remoteSourceMock.fetchCoins() }
            coVerify { localSourceMock.storeCoins(coins) }
        }

    @Test
    fun `getCoins fetches and caches the coins from the network when the force flag is set`() =
        runBlocking {
            // Arrange
            val coins = listOf(Bitcoin, Ethereum, Ripple)
            coEvery { remoteSourceMock.fetchCoins() } returns coins
            coEvery { localSourceMock.getStoredCoins() } returns listOf(Ripple, Ethereum)

            // Act
            val result = sut.getCoins(forceNonCached = true)

            // Assert
            result.rightValue shouldBe coins
            coVerify { remoteSourceMock.fetchCoins() }
            coVerify { localSourceMock.storeCoins(coins) }
        }

    @Test
    fun `getCoins returns the cached coins if they exist`() = runBlocking {
        // Arrange
        val cachedCoins = listOf(Bitcoin, Ripple, Ethereum)
        coEvery { localSourceMock.getStoredCoins() } returns cachedCoins

        // Act
        val result = sut.getCoins()

        // Assert
        result.rightValue shouldBe listOf(Bitcoin, Ripple, Ethereum)
        coVerify { localSourceMock.getStoredCoins() }
        verify { remoteSourceMock wasNot Called }
    }

    @Test
    fun `getCoins returns a Failure when the coin list fetch fails`() =
        runBlocking {
            // Arrange
            val exception = RuntimeException()
            coEvery { remoteSourceMock.fetchCoins() } throws exception
            coEvery { localSourceMock.getStoredCoins() } returns emptyList()

            // Act
            val result = sut.getCoins()

            // Assert
            result.leftValue shouldBeA Failure::class
            coVerify { errorHandlerMock.getFailure(exception) }
        }

    @Test
    fun `getCoins fetches the list of coins from the network if querying the cache fails`() =
        runBlocking {
            // Arrange
            coEvery { localSourceMock.getStoredCoins() } throws RuntimeException()
            coEvery { remoteSourceMock.fetchCoins() } returns listOf(Bitcoin, Ethereum)

            // Act
            val result = sut.getCoins()

            // Assert
            result.rightValue shouldBe listOf(Bitcoin, Ethereum)
        }

    @Test
    fun `getCoins fetches the list of coins from the network and returns them even if the caching fails`() =
        runBlocking {
            // Arrange
            coEvery { remoteSourceMock.fetchCoins() } returns listOf(Bitcoin)
            coEvery { localSourceMock.getStoredCoins() } returns emptyList()
            coEvery { localSourceMock.storeCoins(any()) } throws RuntimeException()

            // Act
            val result = sut.getCoins()

            // Assert
            result.rightValue shouldBe listOf(Bitcoin)
        }

    @Test
    fun `getCoinPrice fetches and caches the coin price from the remote data source when there isn't a cached value`() =
        runBlocking {
            // Arrange
            val price = 9532.472.toPrice()
            coEvery { localSourceMock.getLastCoinPrice(Bitcoin) } returns null
            coEvery { remoteSourceMock.fetchCoinPrice(Bitcoin) } returns price

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe price
            coVerify { localSourceMock.getLastCoinPrice(Bitcoin) }
            coVerify { remoteSourceMock.fetchCoinPrice(Bitcoin) }
            coVerify { localSourceMock.storeCoinPrices(listOf(Bitcoin to price)) }
        }

    @Test
    fun `getCoinPrice returns the cached coin price if it was fetched less than 5 minutes ago`() =
        runBlocking {
            // Arrange
            val price = 321.98.toPrice(
                timestamp = Clock.System.now()
                    .minus(2, MINUTE)
                    .toLocalDateTime(currentSystemDefault())
            )
            coEvery { localSourceMock.getLastCoinPrice(Bitcoin) } returns price

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe price
            verify { remoteSourceMock wasNot Called }
            coVerify { localSourceMock.getLastCoinPrice(Bitcoin) }
        }

    @Test
    fun `getCoinPrice fetches the coin price if the cached one is more than 5 minutes old`() =
        runBlocking {
            // Arrange
            val localPrice = 321.98.toPrice(
                timestamp = Clock.System.now()
                    .minus(1, HOUR)
                    .toLocalDateTime(currentSystemDefault())
            )
            val remotePrice = 500.32.toPrice()
            coEvery { localSourceMock.getLastCoinPrice(Bitcoin) } returns localPrice
            coEvery { remoteSourceMock.fetchCoinPrice(Bitcoin) } returns remotePrice

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe remotePrice
            coVerify { localSourceMock.getLastCoinPrice(Bitcoin) }
            coVerify { remoteSourceMock.fetchCoinPrice(Bitcoin) }
            coVerify { localSourceMock.storeCoinPrices(listOf(Bitcoin to remotePrice)) }
        }

    @Test
    fun `getCoinPrice returns a Failure when it has to fetch the coin price and it fails`() =
        runBlocking {
            // Arrange
            val localPrice = 0.123.toPrice(
                timestamp = Clock.System.now()
                    .minus(2, HOUR, currentSystemDefault())
                    .toLocalDateTime(currentSystemDefault())
            )
            coEvery { remoteSourceMock.fetchCoinPrice(Ripple) } throws RuntimeException()
            coEvery { localSourceMock.getLastCoinPrice(Ripple) } returns localPrice

            // Act
            val result = sut.getCoinPrice(Ripple)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    @Test
    fun `getCoinPrice fetches the price from the network in USD if querying the cache fails`() =
        runBlocking {
            // Arrange
            coEvery { localSourceMock.getLastCoinPrice(Bitcoin) } throws RuntimeException()
            coEvery { remoteSourceMock.fetchCoinPrice(Bitcoin) } returns 20_000.0.toPrice()

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.requireRight().value shouldBeCloseTo 20_000.0
        }

    @Test
    fun `getCoinPrice fetches the price from the network and returns it even if the caching fails`() =
        runBlocking {
            // Arrange
            coEvery { localSourceMock.storeCoinPrices(any()) } throws RuntimeException()
            coEvery { remoteSourceMock.fetchCoinPrice(Bitcoin) } returns 20_000.0.toPrice()

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.requireRight().value shouldBeCloseTo 20_000.0
        }

    @Test
    fun `updateCoin uses the local data source with the received coin`() = runBlocking {
        // Arrange
        coEvery { localSourceMock.updateCoin(any()) } just Runs

        // Act
        sut.updateCoin(Bitcoin)

        // Assert
        coVerify { localSourceMock.updateCoin(Bitcoin) }
    }

    @Test
    fun `updateCoin returns a Failure if the local storage update fails`() = runBlocking {
        // Arrange
        coEvery { localSourceMock.updateCoin(any()) } throws RuntimeException()

        // Act
        val result = sut.updateCoin(Bitcoin)

        // Assert
        result.requireLeft() shouldBeA Failure::class
    }

    //region Helpers
    //endregion

}