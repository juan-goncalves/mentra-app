package me.juangoncalves.mentra.domain.usecases.coin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.juangoncalves.mentra.Left
import me.juangoncalves.mentra.MainCoroutineRule
import me.juangoncalves.mentra.Right
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.extensions.requireRight
import me.juangoncalves.mentra.extensions.toLeft
import me.juangoncalves.mentra.toRight
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FindCoinsByNameTest {

    //region Rules
    @get:Rule val mainCoroutineRule = MainCoroutineRule()
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    //endregion

    //region Mocks
    @MockK lateinit var coinRepositoryMock: CoinRepository
    //endregion

    private lateinit var sut: FindCoinsByName

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = FindCoinsByName(coinRepositoryMock, mainCoroutineRule.dispatcher)
    }

    @Test
    fun `returns the coins matching the query sorted in descending order by the closest match`() =
        runBlockingTest {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("bit")

            // Assert
            val matchingCoins = result.requireRight()
            assertTrue(result is Right)
            assertEquals(3, matchingCoins.size)
            // First result is "BitRow" as its the closest match to "bit" by its length
            assertEquals(availableCoins[1], matchingCoins[0])
            // Second result is "Bitcoin" as is longer than "BitRow"
            assertEquals(availableCoins[0], matchingCoins[1])
            // Final result is "Bitcoin Gold" as its the longest of the results
            assertEquals(availableCoins[4], matchingCoins[2])
        }

    @Test
    fun `ignores starting and ending spaces in the query string`() = runBlockingTest {
        // Arrange
        coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

        // Act
        val result = sut("   bit  ")

        // Assert
        val matchingCoins = result.requireRight()
        assertTrue(result is Right)
        assertEquals(3, matchingCoins.size)
        // First result is "BitRow" as its the closest match to "bit" by its length
        assertEquals(availableCoins[1], matchingCoins[0])
        // Second result is "Bitcoin" as is longer than "BitRow"
        assertEquals(availableCoins[0], matchingCoins[1])
        // Final result is "Bitcoin Gold" as its the longest of the results
        assertEquals(availableCoins[4], matchingCoins[2])
    }

    @Test
    fun `ignores casing in the query and coin names`() = runBlockingTest {
        // Arrange
        coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

        // Act
        val result = sut("BiT")

        // Assert
        val matchingCoins = result.requireRight()
        assertTrue(result is Right)
        assertEquals(3, matchingCoins.size)
        // First result is "BitRow" as its the closest match to "bit" by its length
        assertEquals(availableCoins[1], matchingCoins[0])
        // Second result is "Bitcoin" as is longer than "BitRow"
        assertEquals(availableCoins[0], matchingCoins[1])
        // Final result is "Bitcoin Gold" as its the longest of the results
        assertEquals(availableCoins[4], matchingCoins[2])
    }

    @Test
    fun `returns the complete list of available coins if the query is empty`() =
        runBlockingTest {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("")

            // Assert
            assertEquals(availableCoins, result.requireRight())
        }

    @Test
    fun `returns the complete list of available coins if the query length is 1`() =
        runBlockingTest {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("b")

            // Assert
            assertEquals(availableCoins, result.requireRight())
        }

    @Test
    fun `returns the complete list of available coins if the query length is 2`() =
        runBlockingTest {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns availableCoins.toRight()

            // Act
            val result = sut("bl")

            // Assert
            assertEquals(availableCoins, result.requireRight())
        }

    @Test
    fun `returns a failure if there's an error getting the list of available coins`() =
        runBlockingTest {
            // Arrange
            coEvery { coinRepositoryMock.getCoins() } returns StorageFailure().toLeft()

            // Act
            val result = sut("some query")

            // Assert
            assertTrue(result is Left)
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