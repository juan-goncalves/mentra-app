package me.juangoncalves.mentra.data.repositories

import either.Either
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.errors.WalletCreationFailure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.log.Logger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.isA
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class WalletRepositoryImplTest {

    @get:Rule val mainCoroutineRule = MainCoroutineRule()

    @MockK lateinit var walletLocalDataSource: WalletLocalDataSource
    @MockK lateinit var coinLocalDataSource: CoinLocalDataSource
    @MockK lateinit var loggerMock: Logger

    private lateinit var sut: WalletRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        val walletMapper = WalletMapper(coinLocalDataSource)
        sut = WalletRepositoryImpl(
            mainCoroutineRule.dispatcher,
            mainCoroutineRule.dispatcher,
            walletLocalDataSource,
            walletMapper,
            loggerMock
        )
    }

    @Test
    fun `getWallets returns the wallets from the local data source`() = runBlocking {
        // Arrange
        val btcWalletModel = WalletModel("BTC", 0.781, 1)
        val ethWalletModel = WalletModel("ETH", 1.25, 2)
        coEvery {
            walletLocalDataSource.getAll()
        } returns listOf(btcWalletModel, ethWalletModel)
        coEvery { coinLocalDataSource.findCoinBySymbol("BTC") } returns Bitcoin
        coEvery { coinLocalDataSource.findCoinBySymbol("ETH") } returns Ethereum

        // Act
        val result = sut.getWallets()

        // Assert
        val data = (result as Either.Right).value
        val btcWallet = data.find { it.coin == Bitcoin }
        val ethWallet = data.find { it.coin == Ethereum }
        assertEquals(2, data.size)
        assertNotNull(btcWallet)
        assertNotNull(ethWallet)
        assertThat(btcWallet!!.amount, closeTo(0.781, 0.0001))
        assertThat(ethWallet!!.amount, closeTo(1.25, 0.0001))
    }

    @Test
    fun `getWallets returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalDataSource.getAll() } throws StorageException()

            // act
            val result = sut.getWallets()

            // assert
            val failure = (result as Left).value
            assertTrue(failure is StorageFailure)
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `createWallet uses the local data source to store the received wallet`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45)
            val slot = slot<WalletModel>()
            coEvery { walletLocalDataSource.save(capture(slot)) } just Runs

            // Act
            val result = sut.createWallet(wallet)

            // Assert
            assertNotNull(result.rightValue)
            assertEquals(Bitcoin.symbol, slot.captured.coinSymbol)
            assertThat(slot.captured.amount, closeTo(0.45, 0.0001))
        }

    @Test
    fun `createWallet returns a WalletCreationFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45)
            coEvery { walletLocalDataSource.save(any()) } throws StorageException()

            // Act
            val result = sut.createWallet(wallet)

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is WalletCreationFailure)
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `findWalletsByCoin uses the local data source to find the wallets by coin`() = runBlocking {
        // Arrange
        val walletModel = WalletModel("BTC", 0.781, 1)
        coEvery { walletLocalDataSource.findByCoin(Bitcoin) } returns listOf(walletModel)
        coEvery { coinLocalDataSource.findCoinBySymbol("BTC") } returns Bitcoin

        // Act
        val result = sut.findWalletsByCoin(Bitcoin)

        // Assert
        val data = (result as Right).value
        coVerify { walletLocalDataSource.findByCoin(Bitcoin) }
        assertEquals(1, data.size)
    }

    @Test
    fun `findWalletsByCoin returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalDataSource.findByCoin(any()) } throws StorageException()

            // Act
            val result = sut.findWalletsByCoin(Ripple)

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is StorageFailure)
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `updateWalletValue uses the local data source to store the received wallet value`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val newPrice = Price(Currency.USD, 1.34, LocalDateTime.now())

            // Act
            val result = sut.updateWalletValue(wallet, newPrice)

            // Assert
            assertEquals(true, result is Right)
            coVerify { walletLocalDataSource.updateValue(wallet, newPrice) }
        }

    @Test
    fun `updateWalletValue returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val newPrice = Price(Currency.USD, 1.34, LocalDateTime.now())
            coEvery {
                walletLocalDataSource.updateValue(any(), any())
            } throws StorageException()

            // Act
            val result = sut.updateWalletValue(wallet, newPrice)

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is StorageFailure)
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `deleteWallet passes the id of the wallet to delete to the local data source`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45, 15)
            val slot = slot<WalletModel>()
            coEvery { walletLocalDataSource.delete(capture(slot)) } just Runs

            // Act
            val result = sut.deleteWallet(wallet)

            // Assert
            assertNotNull(result.rightValue)
            assertEquals(15, slot.captured.id)
        }

    @Test
    fun `deleteWallet returns a failure when the local data source throws an exception`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45, 15)
            coEvery { walletLocalDataSource.delete(any()) } throws Exception()

            // Act
            val result = sut.deleteWallet(wallet)

            // Assert
            assertNotNull(result.leftValue)
            assertThat(result.requireLeft(), isA(Failure::class.java))
        }

    /*
        TODO: Test the `getWalletValueHistory` method
              Cases: 1. returns the expected list of prices (mapped from WalletModels)
                     2. failure if the wallet doesn't exist?
                     3. classic failures when a StorageException is thrown
     */

}