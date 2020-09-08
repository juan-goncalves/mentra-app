package me.juangoncalves.mentra.data.repositories

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.log.Logger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class WalletRepositoryImplTest {

    @MockK lateinit var walletLocalDataSource: WalletLocalDataSource
    @MockK lateinit var coinLocalDataSource: CoinLocalDataSource
    @MockK lateinit var loggerMock: Logger

    private lateinit var sut: WalletRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        val walletMapper = WalletMapper(coinLocalDataSource)
        sut = WalletRepositoryImpl(walletLocalDataSource, walletMapper, loggerMock)
    }

    @Test
    fun `getWallets returns the wallets from the local data source`() = runBlocking {
        // Arrange
        val btcWalletModel = WalletModel("BTC", 0.781, 1)
        val ethWalletModel = WalletModel("ETH", 1.25, 2)
        coEvery {
            walletLocalDataSource.getStoredWallets()
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
            coEvery { walletLocalDataSource.getStoredWallets() } throws StorageException()

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

            // Act
            val result = sut.createWallet(wallet)

            // Assert
            coVerify { walletLocalDataSource.storeWallet(wallet) }
            assert(result is Right)
        }

    @Test
    fun `createWallet returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45)
            coEvery { walletLocalDataSource.storeWallet(any()) } throws StorageException()

            // Act
            val result = sut.createWallet(wallet)

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is StorageFailure)
            verify { loggerMock.error(any(), any()) }
        }

    @Test
    fun `findWalletsByCoin uses the local data source to find the wallets by coin`() = runBlocking {
        // Arrange
        val walletModel = WalletModel("BTC", 0.781, 1)
        coEvery { walletLocalDataSource.findWalletsByCoin(Bitcoin) } returns listOf(walletModel)
        coEvery { coinLocalDataSource.findCoinBySymbol("BTC") } returns Bitcoin

        // Act
        val result = sut.findWalletsByCoin(Bitcoin)

        // Assert
        val data = (result as Right).value
        coVerify { walletLocalDataSource.findWalletsByCoin(Bitcoin) }
        assertEquals(1, data.size)
    }

    @Test
    fun `findWalletsByCoin returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalDataSource.findWalletsByCoin(any()) } throws StorageException()

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
            coVerify { walletLocalDataSource.updateWalletValue(wallet, newPrice) }
        }

    @Test
    fun `updateWalletValue returns a StorageFailure when a StorageException is thrown and logs it`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val newPrice = Price(Currency.USD, 1.34, LocalDateTime.now())
            coEvery {
                walletLocalDataSource.updateWalletValue(any(), any())
            } throws StorageException()

            // Act
            val result = sut.updateWalletValue(wallet, newPrice)

            // Assert
            val failure = (result as Left).value
            assertTrue(failure is StorageFailure)
            verify { loggerMock.error(any(), any()) }
        }

}