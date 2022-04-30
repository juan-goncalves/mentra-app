package me.juangoncalves.mentra.android_cache.sources

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.android_cache.*
import me.juangoncalves.mentra.android_cache.daos.CoinDao
import me.juangoncalves.mentra.android_cache.daos.CoinPriceDao
import me.juangoncalves.mentra.android_cache.entities.CoinPriceEntity
import me.juangoncalves.mentra.android_cache.mappers.CoinMapper
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
        coinDao.upsertAll(BitcoinEntity, EthereumEntity, RippleEntity)

        // Act
        val result = sut.getStoredCoins()

        // Assert
        result.size shouldBe 3
    }

    @Test
    fun `clearCoins should delete every coin in the database`() = runBlocking {
        // Arrange
        coinDao.upsertAll(BitcoinEntity, EthereumEntity, RippleEntity)

        // Act
        sut.clearCoins()

        // Assert
        val stored = coinDao.getAll()
        stored.size shouldBe 0
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

    @Test
    fun `storeCoins should update the coins on conflicts`() =
        runBlocking {
            // Arrange
            coinDao.upsertAll(BitcoinEntity, EthereumEntity, RippleEntity)

            val updates = listOf(
                Bitcoin.copy(name = "Bitcoin2", position = 10),
                Ethereum.copy(position = 11)
            )

            // Act
            sut.storeCoins(updates)

            // Assert
            val expected = listOf(
                RippleEntity,
                BitcoinEntity.copy(name = "Bitcoin2", position = 10),
                EthereumEntity.copy(position = 11),
            )

            coinDao.getAll() shouldBe expected
        }

    @Test
    fun `storeCoinPrices should store the coin prices in the database`() = runBlocking {
        // Arrange
        coinDao.upsertAll(BitcoinEntity)
        val btcPrice = 8765.321.toPrice()
        val prices = listOf(Bitcoin to btcPrice)

        // Act
        sut.storeCoinPrices(prices)

        // Assert
        val latestBtcPrice = coinPriceDao.getMostRecentCoinPrice(Bitcoin.symbol)
        latestBtcPrice!!.valueInUSD shouldBeCloseTo btcPrice.value
    }

    @Test(expected = IllegalArgumentException::class)
    fun `storeCoinPrices should throw an IllegalArgumentException if any currency is not USD`() =
        runBlocking {
            // Arrange
            coinDao.upsertAll(BitcoinEntity)
            val price = 8765.321.toPrice(currency = EUR)

            // Act
            sut.storeCoinPrices(listOf(Bitcoin to price))

            // Assert
        }

    @Test
    fun `getLastCoinPrice should return the most recent coin price stored in the database`() =
        runBlocking {
            // Arrange
            coinDao.upsertAll(BitcoinEntity)
            val prices = arrayOf(
                CoinPriceEntity("BTC", 20.5.toBigDecimal(), LocalDateTime.of(2020, 6, 23, 5, 30)),
                CoinPriceEntity("BTC", 738.5.toBigDecimal(), LocalDateTime.of(2020, 8, 13, 9, 30)),
                CoinPriceEntity("BTC", 245.5.toBigDecimal(), LocalDateTime.of(2019, 1, 23, 5, 30))
            )
            prices.forEach { coinPriceDao.insertCoinPrices(it) }

            // Act
            val result = sut.getLastCoinPrice(Bitcoin)

            // Assert
            result!!.value shouldBeCloseTo 738.5.toBigDecimal()
            result.currency shouldBe USD
        }

    @Test
    fun `findCoinBySymbol should return the coin if is stored in the database`() = runBlocking {
        // Arrange
        coinDao.upsertAll(BitcoinEntity, EthereumEntity, RippleEntity)

        // Act
        val result = sut.findCoinBySymbol("BTC")

        // Assert
        result shouldBe Bitcoin
    }

    @Test
    fun `findCoinBySymbol should return null if the coin is not in the database`() = runBlocking {
        // Arrange
        coinDao.upsertAll(BitcoinEntity, EthereumEntity, RippleEntity)

        // Act
        val result = sut.findCoinBySymbol("NONE")

        // Assert
        result shouldBe null
    }

    //region Helpers
    //endregion

}