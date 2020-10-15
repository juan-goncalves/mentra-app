package me.juangoncalves.mentra.domain.usecases.wallet

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.domain.errors.FetchPriceFailure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.leftValue
import me.juangoncalves.mentra.extensions.rightValue
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class RefreshWalletValueTest {

    @MockK private lateinit var coinRepositoryMock: CoinRepository
    @MockK private lateinit var walletRepositoryMock: WalletRepository

    private lateinit var sut: RefreshWalletValue

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut =
            RefreshWalletValue(
                coinRepositoryMock,
                walletRepositoryMock
            )
    }

    @Test
    fun `returns the correct wallet value for the given coin price`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ripple, 125.22, 39)
        val timestamp = LocalDateTime.now()
        val ripplePrice = Price(Currency.USD, 1.20, timestamp)
        coEvery { coinRepositoryMock.getCoinPrice(Ripple, Currency.USD) } returns Right(ripplePrice)
        coEvery { walletRepositoryMock.updateWalletValue(any(), any()) } returns Right(Unit)

        // Act
        val result = sut(wallet)

        // Assert
        assertTrue(result is Right)
        assertThat(result.rightValue!!.value, closeTo(150.264, 0.0001))
        assertEquals(result.rightValue!!.currency, Currency.USD)
        assertEquals(result.rightValue!!.date, timestamp)
    }

    @Test
    fun `records the updated wallet value using the WalletRepository`() = runBlocking {
        // Arrange
        val slot = slot<Price>()
        val wallet = Wallet(Ethereum, 2.16, 15)
        val timestamp = LocalDateTime.now()
        val ethPrice = Price(Currency.USD, 381.20, timestamp)
        coEvery { coinRepositoryMock.getCoinPrice(Ethereum, Currency.USD) } returns Right(ethPrice)
        coEvery { walletRepositoryMock.updateWalletValue(any(), capture(slot)) } returns Right(Unit)

        // Act
        sut(wallet)

        // Assert
        coVerify { walletRepositoryMock.updateWalletValue(wallet, any()) }
        assertThat(slot.captured.value, closeTo(823.392, 0.0001))
        assertEquals(slot.captured.currency, Currency.USD)
        assertEquals(slot.captured.date, timestamp)
    }

    @Test
    fun `returns a FetchPriceFailure if the coin price is not found`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ethereum, 2.16, 15)
        coEvery { coinRepositoryMock.getCoinPrice(any(), any()) } returns Left(FetchPriceFailure())

        // Act
        val result = sut(wallet)

        // Assert
        assertTrue(result is Left)
        assertTrue(result.leftValue is FetchPriceFailure)
    }

    @Test
    fun `returns a StorageFailure if the updated wallet value can't be saved`() = runBlocking {
        // Arrange
        val wallet = Wallet(Ethereum, 2.16, 15)

        coEvery {
            coinRepositoryMock.getCoinPrice(Ethereum, Currency.USD)
        } returns Right(USDPrices[Ethereum]!!)

        coEvery {
            walletRepositoryMock.updateWalletValue(any(), any())
        } returns Left(StorageFailure())

        // Act
        val result = sut(wallet)

        // Assert
        assertTrue(result is Left)
        assertTrue(result.leftValue is StorageFailure)
    }

}