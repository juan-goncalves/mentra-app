package me.juangoncalves.mentra.features.wallet_management.data.repositories

import either.Either
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.features.wallet_management.Bitcoin
import me.juangoncalves.mentra.features.wallet_management.Ethereum
import me.juangoncalves.mentra.features.wallet_management.Ripple
import me.juangoncalves.mentra.features.wallet_management.data.models.CoinSchema
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
                CoinSchema("BTC", "http://url.com/btc.jpg", "Bitcoin", false),
                CoinSchema("ETH", "http://url.com/eth.jpg", "Ethereum", false),
                CoinSchema("XRP", "http://url.com/xrp.jpg", "Ripple", false)
            )
            val coins = listOf(Bitcoin, Ethereum, Ripple)
            `when`(remoteDataSource.fetchCoins()).thenReturn(schemas)
            `when`(localDataSource.getStoredCoins()).thenReturn(emptyList())

            // Act
            val result = coinRepository.getCoins()

            // Assert
            val resultData = result as Either.Right
            verify(remoteDataSource).fetchCoins()
            verify(localDataSource).storeCoins(coins)
            assertEquals(coins, resultData.value)
        }

    // 2. Returns the coins from the local storage when it isn't empty without touching the remote
    //    data source

    // 3. Returns a Either.Left ServerFailure when the remote server throws a ServerException

    // 4. Returns a Either.Left LocalStorageFailure when the local source returns a LocalStorageException

    // 5. Maybe return a failure that stores a Money object when the fetch price from the remote
    //    source fails so that we can show a warning to the user indicating that it isn't the most
    //    recent price / he has no internet / shit failed.

}