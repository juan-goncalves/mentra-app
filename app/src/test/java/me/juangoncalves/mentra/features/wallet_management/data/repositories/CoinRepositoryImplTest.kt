package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.errors.ServerFailure
import me.juangoncalves.mentra.core.errors.StorageException
import me.juangoncalves.mentra.core.errors.StorageFailure
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.wallet_management.*
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

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
    fun `getCoins returns a StorageFailure when a StorageException is thrown while reading the cached coins`() =
        runBlocking {
            // Arrange
            `when`(localDataSource.getStoredCoins()).thenThrow(StorageException())

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val failure = (result as Either.Left).value
            assertTrue(failure is StorageFailure)
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

    // 6. Maybe return a failure that stores a Money object when the fetch price from the remote
    //    source fails so that we can show a warning to the user indicating that it isn't the most
    //    recent price / he has no internet / shit failed.

}