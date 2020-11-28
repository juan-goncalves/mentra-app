package me.juangoncalves.mentra.data.repositories

import either.Either
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain.errors.*
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.log.Logger
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class CoinRepositoryImplTest {

    @get:Rule val mainCoroutineRule = MainCoroutineRule()

    @MockK lateinit var loggerMock: Logger
    @MockK lateinit var localDataSource: CoinLocalDataSource
    @MockK lateinit var remoteDataSource: CoinRemoteDataSource

    private lateinit var coinRepository: CoinRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        coinRepository = CoinRepositoryImpl(
            remoteDataSource,
            localDataSource,
            CoinMapper(),
            loggerMock,
            mainCoroutineRule.dispatcher
        )
    }

    @Test
    fun `getCoins fetches and caches the coins from the network when the local storage is empty`() =
        runBlockingTest {
            // Arrange
            val coins = listOf(Bitcoin, Ethereum, Ripple)
            coEvery { remoteDataSource.fetchCoins() } returns coins
            coEvery { localDataSource.getStoredCoins() } returns emptyList()

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val resultValue = (result as Right).value
            coVerify { remoteDataSource.fetchCoins() }
            coVerify { localDataSource.storeCoins(coins) }
            assertEquals(coins, resultValue)
        }

    @Test
    fun `getCoins returns the cached coins when the cache is populated`() = runBlockingTest {
        // Arrange
        val models = listOf(BitcoinModel, RippleModel, EthereumModel)
        coEvery { localDataSource.getStoredCoins() } returns models

        // Act
        val result = coinRepository.getCoins()

        // Assert
        val resultValue = (result as Either.Right).value
        coVerify { localDataSource.getStoredCoins() }
        verify { remoteDataSource wasNot Called }
        assertEquals(listOf(Bitcoin, Ripple, Ethereum), resultValue)
    }

    @Test
    fun `getCoins returns a ServerFailure when the remote data source throws a ServerException`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteDataSource.fetchCoins() } throws ServerException()
            coEvery { localDataSource.getStoredCoins() } returns emptyList()

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is ServerFailure)
        }

    @Test
    fun `getCoins should return a InternetConnectionFailure if there's no internet connection while trying to fetch coins`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteDataSource.fetchCoins() } throws InternetConnectionException()
            coEvery { localDataSource.getStoredCoins() } returns emptyList()

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is InternetConnectionFailure)
        }

    @Test
    fun `getCoins tries to fetch coins from the network when a StorageException is thrown and logs the situation`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteDataSource.fetchCoins() } returns listOf(Bitcoin, Ethereum)
            coEvery { localDataSource.getStoredCoins() } throws StorageException()

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val value = (result as Right).value
            coVerify { loggerMock.warning(any(), any()) }
            assertEquals(listOf(Bitcoin, Ethereum), value)
        }

    @Test
    fun `getCoins returns the network fetched coins when a StorageException is thrown while caching them`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteDataSource.fetchCoins() } returns listOf(Bitcoin)
            coEvery { localDataSource.getStoredCoins() } returns emptyList()
            coEvery { localDataSource.storeCoins(any()) } throws StorageException()

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val value = (result as Either.Right).value
            coVerify { loggerMock.warning(any(), any()) }
            assertEquals(listOf(Bitcoin), value)
        }

    @Test
    fun `getCoinPrice fetches and caches the coin price from the remote data source when there isn't a cached value`() =
        runBlockingTest {
            // Arrange
            val price = 9532.472.toPrice()
            coEvery { localDataSource.getLastCoinPrice(Bitcoin) } throws PriceCacheMissException()
            coEvery { remoteDataSource.fetchCoinPrice(Bitcoin) } returns price

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe price
            coVerify { localDataSource.getLastCoinPrice(Bitcoin) }
            coVerify { remoteDataSource.fetchCoinPrice(Bitcoin) }
            coVerify { localDataSource.storeCoinPrice(Bitcoin, price) }
        }

    @Test
    fun `getCoinPrice returns the cached coin price if it was obtained less than 5 minutes ago`() =
        runBlockingTest {
            // Arrange
            val price = 321.98.toPrice(timestamp = LocalDateTime.now().minusMinutes(2))
            coEvery { localDataSource.getLastCoinPrice(Bitcoin) } returns price

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe price
            verify { remoteDataSource wasNot Called }
            coVerify { localDataSource.getLastCoinPrice(Bitcoin) }
        }

    @Test
    fun `getCoinPrice fetches the coin price if the cached one is more than 5 minutes old`() =
        runBlockingTest {
            // Arrange
            val localPrice = 321.98.toPrice(timestamp = LocalDateTime.now().minusHours(1))
            val remotePrice = 500.32.toPrice()
            coEvery { localDataSource.getLastCoinPrice(Bitcoin) } returns localPrice
            coEvery { remoteDataSource.fetchCoinPrice(Bitcoin) } returns remotePrice

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe remotePrice
            coVerify { localDataSource.getLastCoinPrice(Bitcoin) }
            coVerify { remoteDataSource.fetchCoinPrice(Bitcoin) }
            coVerify { localDataSource.storeCoinPrice(Bitcoin, remotePrice) }
        }

    @Test
    fun `getCoinPrice returns a FetchPriceError with the most recent stored coin price when a ServerException is thrown`() =
        runBlockingTest {
            // Arrange
            val localPrice = 0.123.toPrice(timestamp = LocalDateTime.now().minusHours(2))
            coEvery { remoteDataSource.fetchCoinPrice(Ripple) } throws ServerException()
            coEvery { localDataSource.getLastCoinPrice(Ripple) } returns localPrice

            // Act
            val result = coinRepository.getCoinPrice(Ripple)

            // Assert
            val failure = (result as Left).value as FetchPriceFailure
            assertNotNull(failure.storedPrice)
            assertEquals(failure.storedPrice, localPrice)
        }

    @Test
    fun `getCoinPrice returns a FetchPriceError without a price when a ServerException is thrown and there isn't a stored coin price`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteDataSource.fetchCoinPrice(Ripple) } throws ServerException()
            coEvery { localDataSource.getLastCoinPrice(Ripple) } throws PriceCacheMissException()

            // Act
            val result = coinRepository.getCoinPrice(Ripple)

            // Assert
            val failure = (result as Left).value as FetchPriceFailure
            assertNull(failure.storedPrice)
        }

}