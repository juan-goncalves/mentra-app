package me.juangoncalves.mentra.data_layer.repositories

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.data_layer.*
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.leftValue
import me.juangoncalves.mentra.domain_layer.extensions.requireLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.models.Price
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
    @MockK lateinit var errorHandler: ErrorHandler
    //endregion

    private lateinit var sut: WalletRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = WalletRepositoryImpl(walletLocalSourceMock, errorHandler)
        every { errorHandler.getFailure(any()) } returns Failure.Unknown
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
    fun `getWallets returns a Failure if querying the local data source fails`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalSourceMock.getAll() } throws RuntimeException()

            // Act
            val result = sut.getWallets()

            // Assert
            result.leftValue shouldBeA Failure::class
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
    fun `createWallet returns a Failure when the local data source fails`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45)
            coEvery { walletLocalSourceMock.save(any()) } throws RuntimeException()

            // Act
            val result = sut.createWallet(wallet)

            // Assert
            result.leftValue shouldBeA Failure::class
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
    fun `findWalletsByCoin returns a Failure f querying the local data source fails`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalSourceMock.findByCoin(any()) } throws RuntimeException()

            // Act
            val result = sut.findWalletsByCoin(Ripple)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    @Test
    fun `updateWallet uses the local data source to store the received wallet updates`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val price = 1.34.toPrice()

            // Act
            sut.updateWallet(wallet, price)

            // Assert
            coVerify { walletLocalSourceMock.update(wallet, price) }
        }

    @Test
    fun `updateWallet returns a failure when the local data source operation fails`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Ripple, 20.781, 1)
            val price = 1.34.toPrice()
            coEvery { walletLocalSourceMock.update(any(), any()) } throws RuntimeException()

            // Act
            val result = sut.updateWallet(wallet, price)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    @Test
    fun `deleteWallet passes the wallet to delete to the local data source`() =
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
    fun `deleteWallet returns a failure when the local data source operation fails`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 0.45, 15)
            coEvery { walletLocalSourceMock.delete(any()) } throws RuntimeException()

            // Act
            val result = sut.deleteWallet(wallet)

            // Assert
            result.leftValue shouldBeA Failure::class
        }

    @Test
    fun `findWalletById queries the local data source with the received id`() = runBlocking {
        // Arrange
        val wallet = Wallet(Bitcoin, 1.2343, 928)
        coEvery { walletLocalSourceMock.findById(928) } returns wallet

        // Act
        val result = sut.findWalletById(928)

        // Assert
        coVerify { walletLocalSourceMock.findById(928) }
        result.requireRight() shouldBe wallet
    }

    @Test
    fun `findWalletById returns null when there is no matching wallet for the received id`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalSourceMock.findById(any()) } returns null

            // Act
            val result = sut.findWalletById(928)

            // Assert
            result.requireRight() shouldBe null
        }

    @Test
    fun `findWalletById returns a failure when the local data source operation fails`() =
        runBlocking {
            // Arrange
            coEvery { walletLocalSourceMock.findById(any()) } throws RuntimeException()

            // Act
            val result = sut.findWalletById(928)

            // Assert
            result.requireLeft() shouldBeA Failure::class
        }

    @Test
    fun `getWalletValueHistory queries the local data source with the received wallet`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 11.234, 32)
            val history = listOf(
                Price(1_000.0.toBigDecimal(), USD, LocalDateTime(2020, 10, 13, 22, 30)),
                Price(927.0.toBigDecimal(), USD, LocalDateTime(2020, 10, 12, 22, 10))
            )
            coEvery { walletLocalSourceMock.getValueHistory(wallet) } returns history

            // Act
            val result = sut.getWalletValueHistory(wallet)

            // Assert
            coVerify { walletLocalSourceMock.getValueHistory(wallet) }
            result.requireRight() shouldBe history
        }

    @Test
    fun `getWalletValueHistory returns a failure when the local data source operation fails`() =
        runBlocking {
            // Arrange
            val wallet = Wallet(Bitcoin, 11.234, 32)
            coEvery { walletLocalSourceMock.getValueHistory(any()) } throws RuntimeException()

            // Act
            val result = sut.getWalletValueHistory(wallet)

            // Assert
            result.requireLeft() shouldBeA Failure::class
        }

    //region Helpers
    //endregion

}