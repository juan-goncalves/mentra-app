package me.juangoncalves.mentra.domain_layer.usecases.wallet

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.domain_layer.*
import me.juangoncalves.mentra.domain_layer.errors.FetchPriceFailure
import me.juangoncalves.mentra.domain_layer.errors.StorageFailure
import me.juangoncalves.mentra.domain_layer.extensions.*
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class RefreshWalletValueTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var coinRepoMock: CoinRepository
    @MockK lateinit var walletRepoMock: WalletRepository
    //endregion

    private lateinit var sut: RefreshWalletValue

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = RefreshWalletValue(coinRepoMock, walletRepoMock)
    }

    @Test
    fun `returns the correct wallet value for the given coin price`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ripple, 125.22, 39)
        val ts = LocalDateTime.now()
        val ripplePrice = 1.20.toPrice(timestamp = ts)
        coEvery { coinRepoMock.getCoinPrice(Ripple) } returns ripplePrice.toRight()
        coEvery { walletRepoMock.updateWallet(any(), any()) } returns Unit.toRight()

        // Act
        val result = sut(wallet)

        // Assert
        with(result.requireRight()) {
            value shouldBeCloseTo 150.264
            currency shouldBe USD
            timestamp shouldBe ts
        }
    }

    @Test
    fun `records the updated wallet value using the WalletRepository`() = runBlocking {
        // Arrange
        val slot = slot<Price>()
        val wallet = Wallet(Ethereum, 2.16, 15)
        val ts = LocalDateTime.now()
        val ethPrice = 381.20.toPrice(timestamp = ts)
        coEvery { coinRepoMock.getCoinPrice(Ethereum) } returns ethPrice.toRight()
        coEvery { walletRepoMock.updateWallet(any(), capture(slot)) } returns Unit.toRight()

        // Act
        sut(wallet)

        // Assert
        coVerify { walletRepoMock.updateWallet(wallet, any()) }
        with(slot.captured) {
            value shouldBeCloseTo 823.392
            currency shouldBe USD
            timestamp shouldBe ts
        }
    }

    @Test
    fun `returns a FetchPriceFailure if the coin price is not found`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ethereum, 2.16, 15)
        coEvery { coinRepoMock.getCoinPrice(any()) } returns Left(FetchPriceFailure())

        // Act
        val result = sut(wallet)

        // Assert
        result.leftValue shouldBeA FetchPriceFailure::class
    }

    @Test
    fun `returns a StorageFailure if the updated wallet value can't be saved`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ethereum, 2.16, 15)
        coEvery { coinRepoMock.getCoinPrice(Ethereum) } returns Right(USDPrices[Ethereum]!!)
        coEvery { walletRepoMock.updateWallet(any(), any()) } returns StorageFailure().toLeft()

        // Act
        val result = sut(wallet)

        // Assert
        result.leftValue shouldBeA StorageFailure::class
    }

    //region Helpers
    //endregion

}