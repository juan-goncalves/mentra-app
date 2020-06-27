package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.core.errors.CacheMissException
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.errors.ServerFailure
import me.juangoncalves.mentra.core.errors.StorageException
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.wallet_management.*
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency.USD
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CoinRepositoryImplTest {

    @MockK lateinit var loggerMock: Logger
    @MockK lateinit var remoteDataSource: CoinRemoteDataSource
    @MockK lateinit var localDataSource: CoinLocalDataSource

    private lateinit var coinRepository: CoinRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        setupLoggerMock()
        coinRepository = CoinRepositoryImpl(remoteDataSource, localDataSource, loggerMock)
    }

    @Test
    fun `getCoins fetches and caches the coins from the network when the local storage is empty`() =
        runBlocking {
            // Arrange
            val schemas = listOf(BitcoinSchema, EthereumSchema, RippleSchema)
            val coins = listOf(Bitcoin, Ethereum, Ripple)
            coEvery { remoteDataSource.fetchCoins() } returns schemas
            coEvery { localDataSource.getStoredCoins() } returns emptyList()
            coEvery { localDataSource.storeCoins(any()) } just Runs

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
            coEvery { remoteDataSource.fetchCoins() } returns listOf(BitcoinSchema, EthereumSchema)
            coEvery { localDataSource.getStoredCoins() } throws StorageException()
            coEvery { localDataSource.storeCoins(any()) } just Runs

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
            coEvery { remoteDataSource.fetchCoins() } returns listOf(BitcoinSchema)
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
            val price = Price(USD, 9532.472, Now)
            coEvery { localDataSource.getLastCoinPrice(Bitcoin, USD) } throws CacheMissException()
            coEvery { remoteDataSource.fetchCoinPrice(Bitcoin, USD) } returns price
            coEvery { localDataSource.storeCoinPrice(any(), any()) } just Runs

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, USD)

            // Assert
            val data = (result as Either.Right).value
            coVerify { localDataSource.getLastCoinPrice(Bitcoin, USD) }
            coVerify { remoteDataSource.fetchCoinPrice(Bitcoin, USD) }
            coVerify { localDataSource.storeCoinPrice(Bitcoin, price) }
            assertEquals(price, data)
        }

    @Test
    fun `getCoinPrice returns the cached coin price if it was obtained less than 5 minutes ago`() =
        runBlocking {
            // Arrange
            val price = Price(Currency.USD, 321.98, OneMinuteAgo)
            coEvery { localDataSource.getLastCoinPrice(Bitcoin, USD) } returns price

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, Currency.USD)

            // Assert
            val data = (result as Either.Right).value
            verify { remoteDataSource wasNot Called }
            coVerify { localDataSource.getLastCoinPrice(Bitcoin, USD) }
            assertEquals(price, data)
        }

    @Test
    fun `getCoinPrice returns the most recent coin price when a ServerException is thrown`() =
        runBlocking {
            assertFalse(true)
        }

    @Test
    fun `getCoinPrice returns a XFailure when a ServerException is thrown and there isn't a cached value`() =
        runBlocking {
            assertFalse(true)
        }


    private fun setupLoggerMock() {
        coEvery { loggerMock.warning(any(), any()) } just Runs
        coEvery { loggerMock.error(any(), any()) } just Runs
        coEvery { loggerMock.info(any(), any()) } just Runs
    }
}