package me.juangoncalves.mentra.features.portfolio.data.sources

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.core.db.AppDatabase
import me.juangoncalves.mentra.core.db.daos.CoinDao
import me.juangoncalves.mentra.core.db.models.CoinPriceModel
import me.juangoncalves.mentra.core.errors.PriceCacheMissException
import me.juangoncalves.mentra.core.errors.StorageException
import me.juangoncalves.mentra.features.portfolio.data.mapper.CoinMapper
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import org.hamcrest.Matchers.closeTo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CoinLocalDataSourceImplTest {

    private lateinit var coinDao: CoinDao
    private lateinit var db: AppDatabase

    private lateinit var sut: CoinLocalDataSourceImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        coinDao = db.coinDao()
        sut = CoinLocalDataSourceImpl(coinDao, CoinMapper())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getStoredCoins_returnsEveryStoredCoin() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel, EthereumModel, RippleModel)

        // Act
        val result = sut.getStoredCoins()

        // Assert
        assertEquals(3, result.size)
    }

    @Test
    fun clearCoins_deletesEveryStoredCoin() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel, EthereumModel, RippleModel)

        // Act
        sut.clearCoins()

        // Assert
        val stored = coinDao.getAll()
        assertEquals(0, stored.size)
    }

    @Test
    fun storeCoins_mapsAndInsertsAllReceivedCoins() = runBlocking {
        // Arrange
        val coins = listOf(Bitcoin, Ethereum, Ripple)

        // Act
        sut.storeCoins(coins)

        // Assert
        val stored = coinDao.getAll()
        val symbols = coins.map { it.symbol }.toHashSet()
        assertEquals(3, stored.size)
        stored.forEach {
            assertTrue(symbols.contains(it.symbol))
        }
    }

    @Test
    fun storeCoinPrice_storesThePriceInTheDatabase() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel)
        val price = Price(Currency.USD, 8765.321, LocalDateTime.now())

        // Act
        sut.storeCoinPrice(Bitcoin, price)

        // Assert
        val latestPrice = coinDao.getCoinPriceHistory(Bitcoin.symbol).first()
        assertThat(latestPrice.valueInUSD, closeTo(price.value, 0.0001))
    }

    @Test(expected = IllegalArgumentException::class)
    fun storeCoinPrice_throwsExceptionIfCurrencyIsNotUSD() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel)
        val price = Price(Currency.EUR, 8765.321, LocalDateTime.now())

        // Act
        sut.storeCoinPrice(Bitcoin, price)
    }

    @Test(expected = StorageException::class)
    fun storeCoinPrice_throwsStorageExceptionIfCoinDoesNotExistInDB() = runBlocking {
        // Arrange
        val price = Price(Currency.USD, 8765.321, LocalDateTime.now())

        // Act
        sut.storeCoinPrice(Bitcoin, price)
    }

    @Test
    fun getLastCoinPrice_returnsTheMostRecentCoinPriceInDB() = runBlocking {
        // Arrange
        coinDao.insertAll(BitcoinModel)
        val prices = arrayOf(
            CoinPriceModel("BTC", 20.5, LocalDateTime.of(2020, 6, 23, 5, 30)),
            CoinPriceModel("BTC", 738.5, LocalDateTime.of(2020, 8, 13, 9, 30)),
            CoinPriceModel("BTC", 245.5, LocalDateTime.of(2019, 1, 23, 5, 30))
        )
        prices.forEach { coinDao.insertCoinPrice(it) }

        // Act
        val result = sut.getLastCoinPrice(Bitcoin)

        // Assert
        assertThat(result.value, closeTo(738.5, 0.0001))
        assertEquals(result.currency, Currency.USD)
    }

    @Test(expected = PriceCacheMissException::class)
    fun getLastCoinPrice_throwsPriceCacheMissExceptionIfThereIsNoStoredPriceInTheDB() =
        runBlocking {
            // Act
            sut.getLastCoinPrice(Bitcoin)
            // Assert
            Unit
        }

}