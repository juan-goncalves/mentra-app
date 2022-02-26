package me.juangoncalves.mentra.android_cache.sources

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.android_cache.*
import me.juangoncalves.mentra.android_cache.daos.CoinDao
import me.juangoncalves.mentra.android_cache.daos.WalletDao
import me.juangoncalves.mentra.android_cache.daos.WalletValueDao
import me.juangoncalves.mentra.android_cache.mappers.CoinMapper
import me.juangoncalves.mentra.android_cache.mappers.WalletMapper
import me.juangoncalves.mentra.android_cache.models.WalletModel
import me.juangoncalves.mentra.android_cache.models.WalletValueModel
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = Application::class, sdk = [28])
class RoomWalletDataSourceTest {

    //region Rules
    //endregion

    //region Mocks
    //endregion

    lateinit var walletDao: WalletDao
    lateinit var coinDao: CoinDao
    lateinit var walletValueDao: WalletValueDao
    lateinit var db: AppDatabase
    lateinit var walletMapper: WalletMapper
    lateinit var sut: RoomWalletDataSource

    @Before
    fun setup() {
        initializeDatabase()
        initializeDAOs()
        initializeSut()
        insertDefaultCoins()
    }

    @After
    fun closeDb() {
        db.close()
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
        val savedBtcWallet = result.find { it.coin == Bitcoin }
        val savedEthWallet = result.find { it.coin == Ethereum }
        result.size shouldBe 2
        savedBtcWallet!!.amount shouldBeCloseTo 0.53
        savedEthWallet!!.amount shouldBeCloseTo 1.0
    }

    @Test
    fun `save inserts the received wallet into the database`() = runBlocking {
        // Arrange
        val wallet = Wallet(Bitcoin, 0.876)

        // Act
        sut.save(wallet)

        // Assert
        val storedWallets = walletDao.getAll()
        val storedWallet = storedWallets.first()
        storedWallets.size shouldBe 1
        storedWallet.coinSymbol shouldBe "BTC"
        storedWallet.amount shouldBeCloseTo 0.876
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
            result.forEach { it.coin shouldBe Bitcoin }
            result[0].amount shouldBeCloseTo 0.22
            result[1].amount shouldBeCloseTo 1.233
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

    @Test
    fun `delete removes the wallet from the database`() = runBlocking {
        // Arrange
        val btcWalletModel = WalletModel("BTC", 0.22, 1)
        val ethWalletModel = WalletModel("ETH", 1.233, 2)
        val xrpWalletModel = WalletModel("XRP", 23.982, 3)
        walletDao.insertAll(btcWalletModel, ethWalletModel, xrpWalletModel)

        // Act
        val toDelete = walletMapper.map(ethWalletModel)
        sut.delete(toDelete)

        // Assert
        val foundWallet = walletDao.getAll().find { it.coinSymbol == "ETH" }
        foundWallet shouldBe null
    }

    // TODO: Test the `getWalletValueHistory` returns the stored WalletValues for a given wallet id
    // TODO: Test the `getWalletValueHistory` when the wallet does not exist
    // TODO: Test the `getWalletValueHistory` wallet values are returned in descending order (by date)
    // TODO: Test the `getWalletValueHistory` exceptions

    //region Helpers
    private fun initializeDAOs() {
        walletDao = db.walletDao()
        coinDao = db.coinDao()
        walletValueDao = db.walletValueDao()
    }

    private fun initializeDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    private fun insertDefaultCoins() = runBlocking {
        val mapper = CoinMapper()
        val models = listOf(Bitcoin, Ripple, Ethereum).map { mapper.map(it) }
        coinDao.upsertAll(*models.toTypedArray())
    }

    private fun initializeSut() {
        val coinDs = RoomCoinDataSource(coinDao, mockk(), CoinMapper())
        walletMapper = WalletMapper(coinDs)
        sut = RoomWalletDataSource(walletDao, walletValueDao, walletMapper)
    }
    //endregion

}