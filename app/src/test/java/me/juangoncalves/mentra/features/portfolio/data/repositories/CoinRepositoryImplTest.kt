package me.juangoncalves.mentra.features.portfolio.data.repositories

import either.Either
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.core.errors.*
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.portfolio.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.portfolio.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency.EUR
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency.USD
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import me.juangoncalves.mentra.features.portfolio.domain.repositories.CoinRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class CoinRepositoryImplTest {

    @MockK lateinit var loggerMock: Logger
    @MockK lateinit var localDataSource: CoinLocalDataSource
    @MockK lateinit var remoteDataSource: CoinRemoteDataSource

    private lateinit var coinRepository: CoinRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        coinRepository = CoinRepositoryImpl(remoteDataSource, localDataSource, loggerMock)
    }

    @Test
    fun `getCoins fetches and caches the coins from the network when the local storage is empty`() =
        runBlocking {
            // Arrange
            val coins = listOf(
                Bitcoin,
                Ethereum,
                Ripple
            )
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
    fun `getCoins returns the cached coins when the cache is populated`() = runBlocking {
        // Arrange
        val models = listOf(
            BitcoinModel,
            RippleModel,
            EthereumModel
        )
        coEvery { localDataSource.getStoredCoins() } returns models

        // Act
        val result = coinRepository.getCoins()

        // Assert
        val resultValue = (result as Either.Right).value
        coVerify { localDataSource.getStoredCoins() }
        verify { remoteDataSource wasNot Called }
        assertEquals(
            listOf(
                Bitcoin,
                Ripple,
                Ethereum
            ), resultValue
        )
    }

    @Test
    fun `getCoins returns a ServerFailure when the remote data source throws a ServerException`() =
        runBlocking {
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
    fun `getCoins tries to fetch coins from the network when a StorageException is thrown and logs the situation`() =
        runBlocking {
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
        runBlocking {
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
        runBlocking {
            // Arrange
            val price = Price(USD, 9532.472, LocalDateTime.now())
            coEvery {
                localDataSource.getLastCoinPrice(
                    Bitcoin,
                    USD
                )
            } throws PriceCacheMissException()
            coEvery { remoteDataSource.fetchCoinPrice(Bitcoin) } returns price

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, USD)

            // Assert
            val data = (result as Either.Right).value
            coVerify { localDataSource.getLastCoinPrice(Bitcoin, USD) }
            coVerify { remoteDataSource.fetchCoinPrice(Bitcoin) }
            coVerify { localDataSource.storeCoinPrice(Bitcoin, price) }
            assertEquals(price, data)
        }

    @Test
    fun `getCoinPrice returns the cached coin price if it was obtained less than 5 minutes ago`() =
        runBlocking {
            // Arrange
            val price = Price(USD, 321.98, LocalDateTime.now().minusMinutes(2))
            coEvery { localDataSource.getLastCoinPrice(Bitcoin, USD) } returns price

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, USD)

            // Assert
            val data = (result as Either.Right).value
            verify { remoteDataSource wasNot Called }
            coVerify { localDataSource.getLastCoinPrice(Bitcoin, USD) }
            assertEquals(price, data)
        }

    @Test
    fun `getCoinPrice fetches the coin price if the cached one is more than 5 minutes old`() =
        runBlocking {
            // Arrange
            val localPrice = Price(USD, 321.98, LocalDateTime.now().minusHours(1))
            val remotePrice = Price(USD, 500.32, LocalDateTime.now())
            coEvery { localDataSource.getLastCoinPrice(Bitcoin, USD) } returns localPrice
            coEvery { remoteDataSource.fetchCoinPrice(Bitcoin) } returns remotePrice

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, USD)

            // Assert
            val data = (result as Either.Right).value
            coVerify { localDataSource.getLastCoinPrice(Bitcoin, USD) }
            coVerify { remoteDataSource.fetchCoinPrice(Bitcoin) }
            coVerify { localDataSource.storeCoinPrice(Bitcoin, remotePrice) }
            assertEquals(remotePrice, data)
        }

    @Test
    fun `getCoinPrice returns a FetchPriceError with the most recent stored coin price when a ServerException is thrown`() =
        runBlocking {
            // Arrange
            val localPrice = Price(EUR, 0.123, LocalDateTime.now().minusHours(2))
            coEvery { remoteDataSource.fetchCoinPrice(Ripple) } throws ServerException()
            coEvery { localDataSource.getLastCoinPrice(Ripple, EUR) } returns localPrice

            // Act
            val result = coinRepository.getCoinPrice(Ripple, EUR)

            // Assert
            val failure = (result as Left).value as FetchPriceError
            assertNotNull(failure.storedPrice)
            assertEquals(failure.storedPrice, localPrice)
        }

    @Test
    fun `getCoinPrice returns a FetchPriceError without a price when a ServerException is thrown and there isn't a stored coin price`() =
        runBlocking {
            // Arrange
            coEvery { remoteDataSource.fetchCoinPrice(Ripple) } throws ServerException()
            coEvery {
                localDataSource.getLastCoinPrice(Ripple, EUR)
            } throws PriceCacheMissException()

            // Act
            val result = coinRepository.getCoinPrice(Ripple, EUR)

            // Assert
            val failure = (result as Left).value as FetchPriceError
            assertNull(failure.storedPrice)
        }
}