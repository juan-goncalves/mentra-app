package me.juangoncalves.mentra.data_layer.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.data_layer.Bitcoin
import me.juangoncalves.mentra.data_layer.Ethereum
import me.juangoncalves.mentra.data_layer.Ripple
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.data_layer.toPrice
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.StorageException
import me.juangoncalves.mentra.domain_layer.errors.StorageFailure
import me.juangoncalves.mentra.domain_layer.errors.WalletCreationFailure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.test_utils.MainCoroutineRule
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WalletRepositoryImplTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    //endregion

    //region Mocks
    @MockK lateinit var walletLocalSourceMock: WalletLocalDataSource
    @MockK lateinit var coinLocalSourceMock: CoinLocalDataSource
    @MockK lateinit var loggerMock: MentraLogger
    //endregion

    private lateinit var sut: WalletRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = WalletRepositoryImpl(walletLocalSourceMock, loggerMock)
    }

    @Test
    fun `getWallets returns the wallets from the local data source`() = runBlocking {
        // Arrange
        val localWallets = listOf(
            Wallet(Bitcoin, 0.781, 1),
            Wallet(Ethereum, 1.25, 2)
        )
        coEvery { walletLocalSourceMock.getAll() } returns localWallets
        coEvery { coinLocalSourceMock.findCoinBySymbol("BTC") } returns Bitcoin
        coEvery { coinLocalSourceMock.findCoinBySymbol("ETH") } returns Ethereum

        // Act
        val result = sut.getWallets()

        // Assert
        result.requireRight() shouldBe localWallets
    }

    @Test
    fun `getWallets returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalSourceMock.getAll() } throws StorageException()

            // Act
            val result = sut.getWallets()

            // Assert
            result.leftValue shouldBeA StorageFailure::class
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `createWallet uses the local data source to store the received wallet`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45)
            val slot = slot<Wallet>()
            coEvery { walletLocalSourceMock.save(capture(slot)) } just Runs

            // Act
            sut.createWallet(wallet)

            // Assert
            slot.captured.coin shouldBe Bitcoin
            slot.captured.amount shouldBeCloseTo 0.45
        }

    @Test
    fun `createWallet returns a WalletCreationFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45)
            coEvery { walletLocalSourceMock.save(any()) } throws StorageException()

            // Act
            val result = sut.createWallet(wallet)

            // Assert
            result.leftValue shouldBeA WalletCreationFailure::class
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `findWalletsByCoin uses the local data source to find the wallets by coin`() = runBlocking {
        // Arrange
        val wallet = Wallet(Bitcoin, 0.781, 1)
        coEvery { walletLocalSourceMock.findByCoin(Bitcoin) } returns listOf(wallet)
        coEvery { coinLocalSourceMock.findCoinBySymbol("BTC") } returns Bitcoin

        // Act
        val result = sut.findWalletsByCoin(Bitcoin)

        // Assert
        result.requireRight().size shouldBe 1
        coVerify { walletLocalSourceMock.findByCoin(Bitcoin) }
    }

    @Test
    fun `findWalletsByCoin returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalSourceMock.findByCoin(any()) } throws StorageException()

            // Act
            val result = sut.findWalletsByCoin(Ripple)

            // Assert
            result.leftValue shouldBeA StorageFailure::class
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `updateWalletValue uses the local data source to store the received wallet value`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val newPrice = 1.34.toPrice()

            // Act
            sut.updateWalletValue(wallet, newPrice)

            // Assert
            coVerify { walletLocalSourceMock.updateValue(wallet, newPrice) }
        }

    @Test
    fun `updateWalletValue returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val newPrice = 1.34.toPrice()
            coEvery { walletLocalSourceMock.updateValue(any(), any()) } throws StorageException()

            // Act
            val result = sut.updateWalletValue(wallet, newPrice)

            // Assert
            result.leftValue shouldBeA StorageFailure::class
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `deleteWallet passes the id of the wallet to delete to the local data source`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45, 15)
            val slot = slot<Wallet>()
            coEvery { walletLocalSourceMock.delete(capture(slot)) } just Runs

            // Act
            sut.deleteWallet(wallet)

            // Assert
            slot.captured.id shouldBe 15
        }

    @Test
    fun `deleteWallet returns a failure when the local data source throws an exception`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45, 15)
            coEvery { walletLocalSourceMock.delete(any()) } throws Exception()

            // Act
            val result = sut.deleteWallet(wallet)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    /*
        TODO: Test the `getWalletValueHistory` method
              Cases: 1. returns the expected list of prices (mapped from WalletModels)
                     2. failure if the wallet doesn't exist?
                     3. classic failures when a StorageException is thrown
     */

    //region Helpers
    //endregion

}