package me.juangoncalves.mentra.data.sources.wallet

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.db.AppDatabase
import me.juangoncalves.mentra.db.daos.WalletDao
import me.juangoncalves.mentra.db.daos.WalletValueDao
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.db.models.WalletValueModel
import me.juangoncalves.mentra.domain_layer.errors.StorageException
import me.juangoncalves.mentra.domain_layer.models.Wallet
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class)
class WalletLocalDataSourceImplTest {

    //region Rules
    //endregion

    //region Mocks
    //endregion

    private lateinit var walletDao: WalletDao
    private lateinit var walletValueDao: WalletValueDao
    private lateinit var db: AppDatabase
    private lateinit var sut: WalletLocalDataSourceImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        insertDefaultCoins()
        walletDao = db.walletDao()
        walletValueDao = db.walletValueDao()
        initializeSut()
    }

    @Test
    fun `getAll returns every stored wallet in the database`() = runBlocking {
        // Arrange
        val btcWallet = WalletModel("BTC", 0.53)
        val ethWallet = WalletModel("ETH", 1.0)
        walletDao.insertAll(btcWallet, ethWallet)

        // Act
        val result = sut.getAll()

        // Assert
        val savedBtcWallet = result.find { it.coinSymbol == "BTC" }
        val savedEthWallet = result.find { it.coinSymbol == "ETH" }
        result.size shouldBe 2
        savedBtcWallet!!.amount shouldBeCloseTo 0.53
        savedEthWallet!!.amount shouldBeCloseTo 1.0
    }

    @Test(expected = StorageException::class)
    fun `getAll throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            walletDao = mockk()
            coEvery { walletDao.getAll() } throws SQLiteException()
            initializeSut()

            // Act
            sut.getAll()

            // Assert
            Unit
        }

    @Test
    fun `save inserts the received wallet into the database`() = runBlocking {
        // Arrange
        val wallet = WalletModel("BTC", 0.876)

        // Act
        sut.save(wallet)

        // Assert
        val storedWallets = walletDao.getAll()
        val storedWallet = storedWallets.first()
        storedWallets.size shouldBe 1
        storedWallet.coinSymbol shouldBe "BTC"
        storedWallet.amount shouldBeCloseTo 0.876
    }

    @Test(expected = StorageException::class)
    fun `save throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            val wallet = WalletModel("BTC", 0.876)
            walletDao = mockk()
            coEvery { walletDao.insertAll(any()) } throws SQLiteException()
            initializeSut()

            // Act
            sut.save(wallet)

            // Assert
        }

    @Test
    fun `findByCoin returns all the wallets that hold the specified coin`() =
        runBlocking {
            // Arrange
            val btcWallet1 = WalletModel("BTC", 0.22)
            val btcWallet2 = WalletModel("BTC", 1.233)
            val ethWallet = WalletModel("ETH", 3.982)
            walletDao.insertAll(btcWallet1, ethWallet, btcWallet2)

            // Act
            val result = sut.findByCoin(Bitcoin)

            // Assert
            result.size shouldBe 2
            result.forEach { it.coinSymbol shouldBe "BTC" }
            result[0].amount shouldBeCloseTo 0.22
            result[1].amount shouldBeCloseTo 1.233
        }

    @Test(expected = StorageException::class)
    fun `findByCoin throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            walletDao = mockk()
            coEvery { walletDao.findByCoin(any()) } throws SQLiteException()
            initializeSut()

            // Act
            sut.findByCoin(Ripple)

            // Assert
            Unit
        }

    @Test
    fun `updateValue inserts the wallet value into the database`() = runBlocking {
        // Arrange
        val model = WalletModel("BTC", 0.22, 1)
        walletDao.insertAll(model)
        val wallet = Wallet(Bitcoin, model.amount, model.id)
        val newValue = 1235.11.toPrice()

        // Act
        sut.updateValue(wallet, newValue)

        // Assert
        with(walletValueDao.getWalletValueHistory(wallet.id)) {
            size shouldBe 1
            first().valueInUSD shouldBeCloseTo 1235.11
        }
    }

    @Test
    fun `updateValue replaces the wallet value of the day if it already exists`() =
        runBlocking {
            // Arrange
            val model = WalletModel("BTC", 0.22, 1)
            walletDao.insertAll(model)
            val wallet = Wallet(Bitcoin, model.amount, model.id)
            val repeatedDay = LocalDate.of(2020, 5, 10)
            walletValueDao.insert(WalletValueModel(wallet.id, 144.45.toBigDecimal(), repeatedDay))
            val newValue = 432.11.toPrice(timestamp = repeatedDay.atStartOfDay())

            // Act
            sut.updateValue(wallet, newValue)

            // Assert
            with(walletValueDao.getWalletValueHistory(wallet.id)) {
                size shouldBe 1
                first().valueInUSD shouldBeCloseTo 432.11
            }
        }

    @Test(expected = StorageException::class)
    fun `updateValue throws a StorageException when the database throws an exception`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 3.21, 1)
            val newValue = 1235.11.toPrice()
            walletValueDao = mockk()
            coEvery { walletValueDao.insert(any()) } throws SQLiteException()
            initializeSut()

            // Act
            sut.updateValue(wallet, newValue)

            // Assert
        }

    @Test
    fun `delete removes the wallet from the database`() = runBlocking {
        // Arrange
        walletDao.insertAll(BTCWalletModel, ETHWalletModel, XRPWalletModel)

        // Act
        sut.delete(ETHWalletModel)

        // Assert
        val foundWallet = walletDao.getAll().find { it.coinSymbol == "ETH" }
        foundWallet shouldBe null
    }

    /*
        TODO: Test the `getWalletValueHistory` method
              Cases: 1. returns the stored WalletValues for a given wallet id
                     2. wallet does not exist
                     3. the wallet values are returned in descending order (by date)
                     4. exceptions
    */


    //region Helpers
    private fun initializeSut() {
        sut = WalletLocalDataSourceImpl(walletDao, walletValueDao)
    }

    private fun insertDefaultCoins() = runBlocking {
        db.coinDao().insertAll(BitcoinModel, EthereumModel, RippleModel)
    }
    //endregion

}