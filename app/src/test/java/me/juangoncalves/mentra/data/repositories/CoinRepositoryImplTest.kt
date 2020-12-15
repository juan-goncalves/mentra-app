package me.juangoncalves.mentra.data.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.*
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.log.Logger
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class CoinRepositoryImplTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    //endregion

    //region Mocks
    @MockK lateinit var loggerMock: Logger
    @MockK lateinit var localSourceMock: CoinLocalDataSource
    @MockK lateinit var remoteSourceMock: CoinRemoteDataSource
    //endregion

    private lateinit var sut: CoinRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        sut = CoinRepositoryImpl(
            remoteSourceMock,
            localSourceMock,
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
    fun `getCoins returns the cached coins when the cache is populated`() = runBlockingTest {
        // Arrange
        val models = listOf(BitcoinModel, RippleModel, EthereumModel)
        coEvery { localSourceMock.getStoredCoins() } returns models

        // Act
        val result = sut.getCoins()

        // Assert
        result.rightValue shouldBe listOf(Bitcoin, Ripple, Ethereum)
        coVerify { localSourceMock.getStoredCoins() }
        verify { remoteSourceMock wasNot Called }
    }

    @Test
    fun `getCoins returns a ServerFailure when the remote data source throws a ServerException`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteSourceMock.fetchCoins() } throws ServerException()
            coEvery { localSourceMock.getStoredCoins() } returns emptyList()

            // Act
            val result = sut.getCoins()

            // Assert
            result.leftValue shouldBeA ServerFailure::class
        }

    @Test
    fun `getCoins should return a InternetConnectionFailure if there's no internet connection while trying to fetch coins`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteSourceMock.fetchCoins() } throws InternetConnectionException()
            coEvery { localSourceMock.getStoredCoins() } returns emptyList()

            // Act
            val result = sut.getCoins()

            // Assert
            result.leftValue shouldBeA InternetConnectionFailure::class
        }

    @Test
    fun `getCoins tries to fetch coins from the network when a StorageException is thrown and logs the situation`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteSourceMock.fetchCoins() } returns listOf(Bitcoin, Ethereum)
            coEvery { localSourceMock.getStoredCoins() } throws StorageException()

            // Act
            val result = sut.getCoins()

            // Assert
            result.rightValue shouldBe listOf(Bitcoin, Ethereum)
            coVerify { loggerMock.warning(any(), any()) }
        }

    @Test
    fun `getCoins returns the network fetched coins when a StorageException is thrown while caching them`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteSourceMock.fetchCoins() } returns listOf(Bitcoin)
            coEvery { localSourceMock.getStoredCoins() } returns emptyList()
            coEvery { localSourceMock.storeCoins(any()) } throws StorageException()

            // Act
            val result = sut.getCoins()

            // Assert
            result.rightValue shouldBe listOf(Bitcoin)
            coVerify { loggerMock.warning(any(), any()) }
        }

    @Test
    fun `getCoinPrice fetches and caches the coin price from the remote data source when there isn't a cached value`() =
        runBlockingTest {
            // Arrange
            val price = 9532.472.toPrice()
            coEvery { localSourceMock.getLastCoinPrice(Bitcoin) } throws PriceCacheMissException()
            coEvery { remoteSourceMock.fetchCoinPrice(Bitcoin) } returns price

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe price
            coVerify { localSourceMock.getLastCoinPrice(Bitcoin) }
            coVerify { remoteSourceMock.fetchCoinPrice(Bitcoin) }
            coVerify { localSourceMock.storeCoinPrice(Bitcoin, price) }
        }

    @Test
    fun `getCoinPrice returns the cached coin price if it was obtained less than 5 minutes ago`() =
        runBlockingTest {
            // Arrange
            val price = 321.98.toPrice(timestamp = LocalDateTime.now().minusMinutes(2))
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
        runBlockingTest {
            // Arrange
            val localPrice = 321.98.toPrice(timestamp = LocalDateTime.now().minusHours(1))
            val remotePrice = 500.32.toPrice()
            coEvery { localSourceMock.getLastCoinPrice(Bitcoin) } returns localPrice
            coEvery { remoteSourceMock.fetchCoinPrice(Bitcoin) } returns remotePrice

            // Act
            val result = sut.getCoinPrice(Bitcoin)

            // Assert
            result.rightValue shouldBe remotePrice
            coVerify { localSourceMock.getLastCoinPrice(Bitcoin) }
            coVerify { remoteSourceMock.fetchCoinPrice(Bitcoin) }
            coVerify { localSourceMock.storeCoinPrice(Bitcoin, remotePrice) }
        }

    @Test
    fun `getCoinPrice returns a FetchPriceError with the most recent stored coin price when a ServerException is thrown`() =
        runBlockingTest {
            // Arrange
            val localPrice = 0.123.toPrice(timestamp = LocalDateTime.now().minusHours(2))
            coEvery { remoteSourceMock.fetchCoinPrice(Ripple) } throws ServerException()
            coEvery { localSourceMock.getLastCoinPrice(Ripple) } returns localPrice

            // Act
            val result = sut.getCoinPrice(Ripple)

            // Assert
            result.leftValue shouldBeA FetchPriceFailure::class
            (result.leftValue as FetchPriceFailure).storedPrice shouldBe localPrice
        }

    @Test
    fun `getCoinPrice returns a FetchPriceError without a price when a ServerException is thrown and there isn't a stored coin price`() =
        runBlockingTest {
            // Arrange
            coEvery { remoteSourceMock.fetchCoinPrice(Ripple) } throws ServerException()
            coEvery { localSourceMock.getLastCoinPrice(Ripple) } throws PriceCacheMissException()

            // Act
            val result = sut.getCoinPrice(Ripple)

            // Assert
            result.leftValue shouldBeA FetchPriceFailure::class
            (result.leftValue as FetchPriceFailure).storedPrice shouldBe null
        }

    //region Helpers
    //endregion

}