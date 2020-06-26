package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
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
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class CoinRepositoryImplTest {

    private lateinit var loggerMock: Logger
    private lateinit var remoteDataSource: CoinRemoteDataSource
    private lateinit var localDataSource: CoinLocalDataSource
    private lateinit var coinRepository: CoinRepository

    @Before
    fun setUp() {
        loggerMock = mock(Logger::class.java)
        remoteDataSource = mock(CoinRemoteDataSource::class.java)
        localDataSource = mock(CoinLocalDataSource::class.java)
        coinRepository = CoinRepositoryImpl(remoteDataSource, localDataSource, loggerMock)
    }

    @Test
    fun `getCoins fetches and caches the coins from the network when the local storage is empty`() =
        runBlocking {
            // Arrange
            val schemas = listOf(BitcoinSchema, EthereumSchema, RippleSchema)
            val coins = listOf(Bitcoin, Ethereum, Ripple)
            `when`(remoteDataSource.fetchCoins()).thenReturn(schemas)
            `when`(localDataSource.getStoredCoins()).thenReturn(emptyList())

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val resultValue = (result as Either.Right).value
            verify(remoteDataSource).fetchCoins()
            verify(localDataSource).storeCoins(coins)
            assertEquals(coins, resultValue)
        }

    @Test
    fun `getCoins returns the cached coins when the cache is populated`() = runBlocking {
        // Arrange
        val models = listOf(BitcoinModel, RippleModel, EthereumModel)
        `when`(localDataSource.getStoredCoins()).thenReturn(models)

        // Act
        val result = coinRepository.getCoins()

        // Assert
        val resultValue = (result as Either.Right).value
        verify(localDataSource).getStoredCoins()
        verifyNoInteractions(remoteDataSource)
        assertEquals(listOf(Bitcoin, Ripple, Ethereum), resultValue)
    }

    @Test
    fun `getCoins returns a ServerFailure when the remote data source throws a ServerException`() =
        runBlocking {
            // Arrange
            `when`(remoteDataSource.fetchCoins()).thenThrow(ServerException())
            `when`(localDataSource.getStoredCoins()).thenReturn(emptyList())

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val failure = (result as Either.Left).value
            assertTrue(failure is ServerFailure)
        }

    @Test
    fun `getCoins tries to fetch coins from the network when a StorageException is thrown while reading the cached coins`() =
        runBlocking {
            // Arrange
            `when`(remoteDataSource.fetchCoins()).thenReturn(listOf(BitcoinSchema, EthereumSchema))
            `when`(localDataSource.getStoredCoins()).thenThrow(StorageException())

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val value = (result as Either.Right).value
            verify(loggerMock).warning(any(), any())
            assertEquals(listOf(Bitcoin, Ethereum), value)
        }

    @Test
    fun `getCoins returns the network fetched coins when a StorageException is thrown while caching them`() =
        runBlocking {
            // Arrange
            `when`(remoteDataSource.fetchCoins()).thenReturn(listOf(BitcoinSchema))
            `when`(localDataSource.getStoredCoins()).thenReturn(emptyList())
            `when`(localDataSource.storeCoins(any())).thenThrow(StorageException())

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val value = (result as Either.Right).value
            verify(loggerMock).warning(any(), any())
            assertEquals(listOf(Bitcoin), value)
        }

    @Test
    fun `getCoinPrice fetches and caches the coin price from the remote data source when there isn't a recently cached value`() =
        runBlocking {
            // Arrange
            val price = Price(Currency.USD, 9532.472, Date())
            `when`(localDataSource.getLastCoinPrice(Bitcoin, Currency.USD))
                .thenThrow(CacheMissException())
            `when`(remoteDataSource.fetchCoinPrice(Bitcoin, Currency.USD)).thenReturn(price)

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, Currency.USD)

            // Assert
            val data = (result as Either.Right).value
            verify(localDataSource).getLastCoinPrice(Bitcoin, Currency.USD)
            verify(remoteDataSource).fetchCoinPrice(Bitcoin, Currency.USD)
            verify(localDataSource).storeCoinPrice(Bitcoin, price)
            assertEquals(price, data)
        }

    @Test
    fun `getCoinPrice returns the cached coin price if it was obtained less than 5 minutes ago`() =
        runBlocking {
            // Arrange
            val price = Price(Currency.USD, 321.98, OneMinuteAgo)
            `when`(localDataSource.getLastCoinPrice(Bitcoin, Currency.USD))
                .thenReturn(price)

            // Act
            val result = coinRepository.getCoinPrice(Bitcoin, Currency.USD)

            // Assert
            val data = (result as Either.Right).value
            verifyNoInteractions(remoteDataSource)
            verify(localDataSource.getLastCoinPrice(Bitcoin, Currency.USD))
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
}