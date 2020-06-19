package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.*
import me.juangoncalves.mentra.features.wallet_management.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.wallet_management.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class CoinRepositoryImplTest {

    private lateinit var remoteDataSource: CoinRemoteDataSource
    private lateinit var localDataSource: CoinLocalDataSource
    private lateinit var coinRepository: CoinRepository

    @Before
    fun setUp() {
        remoteDataSource = mock(CoinRemoteDataSource::class.java)
        localDataSource = mock(CoinLocalDataSource::class.java)
        coinRepository = CoinRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getCoins fetches and caches the coins from the network when the local storage is empty`() =
        runBlocking {
            // Arrange
            val schemas = listOf(
                CoinSchema(
                    "BTC",
                    "http://url.com/btc.jpg",
                    "Bitcoin",
                    false
                ),
                CoinSchema(
                    "ETH",
                    "http://url.com/eth.jpg",
                    "Ethereum",
                    false
                ),
                CoinSchema(
                    "XRP",
                    "http://url.com/xrp.jpg",
                    "Ripple",
                    false
                )
            )
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

    // 3. Returns a Either.Left ServerFailure when the remote server throws a ServerException

    // 4. Returns a Either.Left LocalStorageFailure when the local source returns a LocalStorageException

    // 5. Maybe return a failure that stores a Money object when the fetch price from the remote
    //    source fails so that we can show a warning to the user indicating that it isn't the most
    //    recent price / he has no internet / shit failed.

}