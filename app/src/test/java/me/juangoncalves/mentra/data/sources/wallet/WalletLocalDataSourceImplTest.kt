package me.juangoncalves.mentra.data.sources.wallet

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.db.AppDatabase
import me.juangoncalves.mentra.db.daos.WalletDao
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.models.Wallet
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class WalletLocalDataSourceImplTest {

    private lateinit var walletDao: WalletDao
    private lateinit var db: AppDatabase

    private lateinit var sut: WalletLocalDataSourceImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        insertDefaultCoins()
        walletDao = db.walletDao()
        initializeSut()
    }

    @Test
    fun `getStoredWallets returns every stored wallet in the database`() = runBlocking {
        // Arrange
        val btcWallet = WalletModel("BTC", 0.53)
        val ethWallet = WalletModel("ETH", 1.0)
        walletDao.insertAll(btcWallet, ethWallet)

        // Act
        val result = sut.getStoredWallets()

        // Assert
        val savedBtcWallet = result.find { it.coinSymbol == "BTC" }
        val savedEthWallet = result.find { it.coinSymbol == "ETH" }
        assertEquals(2, result.size)
        assertNotNull(savedBtcWallet)
        assertNotNull(savedEthWallet)
        assertThat(savedBtcWallet!!.amount, closeTo(0.53, 0.0001))
        assertThat(savedEthWallet!!.amount, closeTo(1.0, 0.0001))
    }

    @Test(expected = StorageException::class)
    fun `getStoredWallets throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            walletDao = mockk()
            coEvery { walletDao.getAll() } throws SQLiteException()
            initializeSut()

            // Act
            sut.getStoredWallets()

            // Assert
            Unit
        }

    @Test
    fun `storeWallet maps and inserts the received wallet into the database`() = runBlocking {
        // Arrange
        val wallet = Wallet(Bitcoin, 0.876)

        // Act
        sut.storeWallet(wallet)

        // Assert
        val storedWallets = walletDao.getAll()
        val storedWallet = storedWallets.first()
        assertEquals(1, storedWallets.size)
        assertEquals("BTC", storedWallet.coinSymbol)
        assertThat(storedWallet.amount, closeTo(0.876, 0.0001))
    }

    @Test(expected = StorageException::class)
    fun `storeWallet throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.876)
            walletDao = mockk()
            coEvery { walletDao.insertAll(any()) } throws SQLiteException()
            initializeSut()

            // Act
            sut.storeWallet(wallet)

            // Assert
            Unit
        }

    @Test
    fun `findWalletsByCoin returns all the wallets that hold the specified coin`() =
        runBlocking {
            // Arrange
            val btcWallet1 = WalletModel("BTC", 0.22)
            val btcWallet2 = WalletModel("BTC", 1.233)
            val ethWallet = WalletModel("ETH", 3.982)
            walletDao.insertAll(btcWallet1, ethWallet, btcWallet2)

            // Act
            val result = sut.findWalletsByCoin(Bitcoin)

            // Assert
            assertEquals(2, result.size)
            result.forEach { assertEquals("BTC", it.coinSymbol) }
            assertThat(result[0].amount, closeTo(0.22, 0.0001))
            assertThat(result[1].amount, closeTo(1.233, 0.0001))
        }

    @Test(expected = StorageException::class)
    fun `findWalletsByCoin throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            walletDao = mockk()
            coEvery { walletDao.findByCoin(any()) } throws SQLiteException()
            initializeSut()

            // Act
            sut.findWalletsByCoin(Ripple)

            // Assert
            Unit
        }

    private fun initializeSut() {
        sut = WalletLocalDataSourceImpl(walletDao, WalletMapper(mockk()))
    }

    private fun insertDefaultCoins() = runBlocking {
        db.coinDao().insertAll(BitcoinModel, EthereumModel, RippleModel)
    }

}