package me.juangoncalves.mentra.data.repositories

import either.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.db.models.WalletModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class WalletRepositoryImplTest {

    @MockK lateinit var walletLocalDataSource: WalletLocalDataSource
    @MockK lateinit var coinLocalDataSource: CoinLocalDataSource

    private lateinit var sut: WalletRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        val walletMapper = WalletMapper(coinLocalDataSource)
        sut = WalletRepositoryImpl(walletLocalDataSource, walletMapper)
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

    // TODO: getWallets returns appropriate failure when a StorageException is thrown

}