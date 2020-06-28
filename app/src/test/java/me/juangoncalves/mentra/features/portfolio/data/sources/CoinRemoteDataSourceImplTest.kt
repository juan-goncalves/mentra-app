package me.juangoncalves.mentra.features.portfolio.data.sources

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.portfolio.data.schemas.CoinListSchema
import me.juangoncalves.mentra.fixture
import me.juangoncalves.mentra.moshi
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CoinRemoteDataSourceImplTest {

    @MockK lateinit var apiService: CryptoCompareService
    @MockK lateinit var logger: Logger

    private lateinit var SUT: CoinRemoteDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        SUT = CoinRemoteDataSourceImpl(apiService, logger)
    }

    @Test
    fun `fetchCoins should return the list of CoinSchema when the response is successful`() =
        runBlocking {
            // Arrange
            val schemaMock = setupMockListCoinsSuccess()

            // Act
            val result = SUT.fetchCoins()

            // Assert
            // Remove the image urls as they are converted (covered in another test case)
            assertEquals(
                result.map { it.copy(imageUrl = "") },
                schemaMock.data.values.map { it.copy(imageUrl = "") }
            )
        }

    @Test
    fun `fetchCoins should build the image url of every coin`() =
        runBlocking {
            // Arrange
            setupMockListCoinsSuccess()

            // Act
            val result = SUT.fetchCoins()

            // Assert
            val expectedImageUrls = mapOf(
                "BTC" to "https://www.cryptocompare.com/media/19633/btc.png",
                "ETH" to "https://www.cryptocompare.com/media/20646/eth_logo.png",
                "NANO" to "https://www.cryptocompare.com/media/30001997/untitled-1.png",
                "FAKE" to "https://www.cryptocompare.com/media/9999/fake999.png"
            )
            result.forEach { assertEquals(expectedImageUrls[it.symbol], it.imageUrl) }
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response is not successful`() =
        runBlocking {
            // Arrange
            coEvery { apiService.listCoins() } returns Response.error(404, mockk())

            // Act
            SUT.fetchCoins()

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the api service throws an exception`() =
        runBlocking {
            // Arrange
            coEvery { apiService.listCoins() } throws Exception()

            // Act
            SUT.fetchCoins()

            // Assert
            verify { logger.error(any(), any()) }
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response body is null`() = runBlocking {
        // Arrange
        coEvery { apiService.listCoins() } returns Response.success(null)

        // Act
        SUT.fetchCoins()

        // Assert
        Unit
    }

    private fun setupMockListCoinsSuccess(): CoinListSchema {
        val adapter = moshi.adapter(CoinListSchema::class.java)
        val coinListSchema = adapter.fromJson(fixture("/coin_list.json"))!!
        coEvery { apiService.listCoins() } returns Response.success(coinListSchema)
        return coinListSchema
    }
}