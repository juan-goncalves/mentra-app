package me.juangoncalves.mentra.domain_layer.usecases.coin

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.errors.StorageFailure
import me.juangoncalves.mentra.domain_layer.extensions.*
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeA
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FindCoinsByNameTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var coinRepositoryMock: CoinRepository
    //endregion

    private lateinit var sut: FindCoinsByName

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = FindCoinsByName(coinRepositoryMock)
    }

    @Test
    fun `returns the coins matching the query sorted in descending order by the closest match`() =
        runBlocking {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("bit")

            // Assert
            with(result.requireRight()) {
                size shouldBe 3
                // First result is "BitRow" as its the closest match to "bit" by its length
                get(0) shouldBe availableCoins[1]
                // Second result is "Bitcoin" as is longer than "BitRow"
                get(1) shouldBe availableCoins[0]
                // Final result is "Bitcoin Gold" as its the longest of the results
                get(2) shouldBe availableCoins[4]
            }
        }

    @Test
    fun `ignores starting and ending spaces in the query string`() = runBlocking {
        // Arrange
        coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

        // Act
        val result = sut("   bit  ")

        // Assert
        with(result.requireRight()) {
            size shouldBe 3
            // First result is "BitRow" as its the closest match to "bit" by its length
            get(0) shouldBe availableCoins[1]
            // Second result is "Bitcoin" as is longer than "BitRow"
            get(1) shouldBe availableCoins[0]
            // Final result is "Bitcoin Gold" as its the longest of the results
            get(2) shouldBe availableCoins[4]
        }
    }

    @Test
    fun `ignores casing in the query and coin names`() = runBlocking {
        // Arrange
        coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

        // Act
        val result = sut("BiT")

        // Assert
        with(result.requireRight()) {
            size shouldBe 3
            // First result is "BitRow" as its the closest match to "bit" by its length
            get(0) shouldBe availableCoins[1]
            // Second result is "Bitcoin" as is longer than "BitRow"
            get(1) shouldBe availableCoins[0]
            // Final result is "Bitcoin Gold" as its the longest of the results
            get(2) shouldBe availableCoins[4]
        }
    }

    @Test
    fun `returns the complete list of available coins if the query is empty`() =
        runBlocking {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("")

            // Assert
            result.rightValue shouldBe availableCoins
        }

    @Test
    fun `returns the complete list of available coins if the query length is 1`() =
        runBlocking {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("b")

            // Assert
            result.rightValue shouldBe availableCoins
        }

    @Test
    fun `returns the complete list of available coins if the query length is 2`() =
        runBlocking {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("bl")

            // Assert
            result.rightValue shouldBe availableCoins
        }

    @Test
    fun `returns a failure if there's an error getting the list of available coins`() =
        runBlocking {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns StorageFailure().toLeft()

            // Act
            val result = sut("some query")

            // Assert
            result.leftValue shouldBeA OldFailure::class
        }

    //region Helpers
    private val availableCoins = listOf(
        Coin("Bitcoin", "BTC", ""),
        Coin("BitRow", "BTR", ""),
        Coin("Ethereum", "ETH", ""),
        Coin("Ripple", "XRP", ""),
        Coin("Bitcoin Gold", "BTG", ""),
        Coin("Monero", "MNR", ""),
        Coin("Ripple Sanctum", "XZP", "")
    )
    //endregion

}