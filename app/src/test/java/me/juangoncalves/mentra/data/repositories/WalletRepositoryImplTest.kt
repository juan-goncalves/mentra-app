package me.juangoncalves.mentra.data.repositories

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.log.Logger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

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
}