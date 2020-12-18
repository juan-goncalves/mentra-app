package me.juangoncalves.mentra.android_cache.sources

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.android_cache.*
import me.juangoncalves.mentra.android_cache.daos.CoinDao
import me.juangoncalves.mentra.android_cache.daos.CoinPriceDao
import me.juangoncalves.mentra.android_cache.mappers.CoinMapper
import me.juangoncalves.mentra.android_cache.models.CoinPriceModel
import me.juangoncalves.mentra.domain_layer.errors.PriceCacheMissException
import me.juangoncalves.mentra.domain_layer.errors.StorageException
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class, sdk = [28])
class RoomCoinDataSourceTest {

    //region Rules
    //endregion

    //region Mocks
    //endregion

    private lateinit var coinDao: CoinDao
    private lateinit var coinPriceDao: CoinPriceDao
    private lateinit var db: AppDatabase
    private lateinit var sut: RoomCoinDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        coinDao = db.coinDao()
        coinPriceDao = db.coinPriceDao()
        sut = RoomCoinDataSource(coinDao, coinPriceDao, CoinMapper())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `getStoredCoins should return every coin stored on the database`() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel, EthereumModel, RippleModel)

        // Act
        val result = sut.getStoredCoins()

        // Assert
        result.size shouldBe 3
    }

    @Test(expected = StorageException::class)
    fun `getStoredCoins should throw a StorageException if the database throws an exception`() =
        runBlocking {
            // Arrange
            coinDao = mockk() // Will throw an exception when any of its methods is called
            sut = RoomCoinDataSource(coinDao, coinPriceDao, CoinMapper())

            // Act
            sut.getStoredCoins()

            // Assert
            Unit
        }

    @Test
    fun `clearCoins should delete every coin in the database`() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel, EthereumModel, RippleModel)

        // Act
        sut.clearCoins()

        // Assert
        val stored = coinDao.getAll()
        stored.size shouldBe 0
    }

    @Test(expected = StorageException::class)
    fun `clearCoins should throw a StorageException if the database throws an exception`() =
        runBlocking {
            // Arrange
            coinDao = mockk() // Will throw an exception when any of its methods is called
            sut = RoomCoinDataSource(coinDao, coinPriceDao, CoinMapper())

            // Act
            sut.clearCoins()

            // Assert
            Unit
        }

    @Test
    fun `storeCoins should map and insert all the received coins into the database`() =
        runBlocking {
            // Arrange
            val coins = listOf(Bitcoin, Ethereum, Ripple)

            // Act
            sut.storeCoins(coins)

            // Assert
            val stored = coinDao.getAll()
            val symbols = coins.map { it.symbol }.toHashSet()
            stored.size shouldBe 3
            stored.forEach {
                symbols.contains(it.symbol) shouldBe true
            }
        }

    @Test(expected = StorageException::class)
    fun `storeCoins should throw a StorageException if the database throws an exception`() =
        runBlocking {
            // Arrange
            coinDao = mockk() // Will throw an exception when any of its methods is called
            sut = RoomCoinDataSource(coinDao, coinPriceDao, CoinMapper())

            // Act
            sut.storeCoins(listOf(Bitcoin, Ethereum))

            // Assert
            Unit
        }

    @Test
    fun `storeCoinPrice should store the coin price in the database`() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel)
        val price = 8765.321.toPrice()

        // Act
        sut.storeCoinPrice(Bitcoin, price)

        // Assert
        val latestPrice = coinPriceDao.getCoinPriceHistory(Bitcoin.symbol).first()
        latestPrice.valueInUSD shouldBeCloseTo price.value
    }

    @Test(expected = IllegalArgumentException::class)
    fun `storeCoinPrice should throw an IllegalArgumentException if the currency is not USD`() =
        runBlocking {
            // Arrange
            coinDao.insertAll(BitcoinModel)
            val price = 8765.321.toPrice(currency = EUR)

            // Act
            sut.storeCoinPrice(Bitcoin, price)

            // Assert
        }

    @Test(expected = StorageException::class)
    fun `storeCoinPrice should throw a StorageException if the coin is not in the database`() =
        runBlocking {
            // Arrange
            val price = 8765.321.toPrice()

            // Act
            sut.storeCoinPrice(Bitcoin, price)

            // Assert
        }

    @Test
    fun `getLastCoinPrice should return the most recent coin price stored in the database`() =
        runBlocking {
            // Arrange
            coinDao.insertAll(BitcoinModel)
            val prices = arrayOf(
                CoinPriceModel("BTC", 20.5.toBigDecimal(), LocalDateTime.of(2020, 6, 23, 5, 30)),
                CoinPriceModel("BTC", 738.5.toBigDecimal(), LocalDateTime.of(2020, 8, 13, 9, 30)),
                CoinPriceModel("BTC", 245.5.toBigDecimal(), LocalDateTime.of(2019, 1, 23, 5, 30))
            )
            prices.forEach { coinPriceDao.insertCoinPrice(it) }

            // Act
            val result = sut.getLastCoinPrice(Bitcoin)

            // Assert
            result.value shouldBeCloseTo 738.5.toBigDecimal()
            result.currency shouldBe USD
        }

    @Test(expected = PriceCacheMissException::class)
    fun `getLastCoinPrice should throw a PriceCacheMissException if there's no stored price for the coin in the database`() =
        runBlocking {
            // Act
            sut.getLastCoinPrice(Bitcoin)

            // Assert
            Unit
        }

    @Test(expected = StorageException::class)
    fun `getLastCoinPrice should throw a StorageException if the database throws an exception`() =
        runBlocking {
            // Arrange
            coinDao = mockk() // Will throw an exception when any of its methods is called
            coinPriceDao = mockk()
            sut = RoomCoinDataSource(coinDao, coinPriceDao, CoinMapper())

            // Act
            sut.getLastCoinPrice(Bitcoin)

            // Assert
            Unit
        }

    @Test
    fun `findCoinBySymbol should return the coin if is stored in the database`() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel, EthereumModel, RippleModel)

        // Act
        val result = sut.findCoinBySymbol("BTC")

        // Assert
        result shouldBe Bitcoin
    }

    @Test
    fun `findCoinBySymbol should return null if the coin is not in the database`() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel, EthereumModel, RippleModel)

        // Act
        val result = sut.findCoinBySymbol("NONE")

        // Assert
        result shouldBe null
    }

    @Test(expected = StorageException::class)
    fun `findCoinBySymbol should throw a StorageException if there's an error communicating with the database`() =
        runBlocking {
            // Arrange
            coinDao = mockk()
            coEvery { coinDao.getCoinBySymbol(any()) } throws SQLiteException()
            sut = RoomCoinDataSource(coinDao, coinPriceDao, CoinMapper())

            // Act
            sut.findCoinBySymbol("BTC")

            // Assert
            Unit
        }

    //region Helpers
    //endregion

}