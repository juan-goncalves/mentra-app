package me.juangoncalves.mentra.features.portfolio.data.sources

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.InstrumentationHelpers
import me.juangoncalves.mentra.InstrumentationHelpers.Bitcoin
import me.juangoncalves.mentra.InstrumentationHelpers.BitcoinModel
import me.juangoncalves.mentra.core.db.AppDatabase
import me.juangoncalves.mentra.core.db.daos.CoinDao
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
import java.io.IOException
import java.time.LocalDateTime

// TODO: Move to unit test implementing a fake for the CoinDao interface
@RunWith(AndroidJUnit4::class)
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
        coinDao.insertAll(
            InstrumentationHelpers.BitcoinModel,
            InstrumentationHelpers.EthereumModel,
            InstrumentationHelpers.RippleModel
        )

        // Act
        val result = sut.getStoredCoins()

        // Assert
        assertEquals(3, result.size)
    }

    @Test
    fun clearCoins_deletesEveryStoredCoin() = runBlocking {
        // Arrange
        coinDao.insertAll(
            InstrumentationHelpers.BitcoinModel,
            InstrumentationHelpers.EthereumModel,
            InstrumentationHelpers.RippleModel
        )

        // Act
        sut.clearCoins()

        // Assert
        val stored = coinDao.getAll()
        assertEquals(0, stored.size)
    }

    @Test
    fun storeCoins_mapsAndInsertsAllReceivedCoins() = runBlocking {
        // Arrange
        val coins = listOf(
            InstrumentationHelpers.Bitcoin,
            InstrumentationHelpers.Ethereum,
            InstrumentationHelpers.Ripple
        )

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

}