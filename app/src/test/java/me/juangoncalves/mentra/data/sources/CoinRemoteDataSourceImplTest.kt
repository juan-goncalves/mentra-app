package me.juangoncalves.mentra.data.sources

import com.squareup.moshi.Types
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.*
import me.juangoncalves.mentra.data.mapper.CoinMapper
import me.juangoncalves.mentra.domain.errors.InternetConnectionException
import me.juangoncalves.mentra.domain.errors.ServerException
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.log.Logger
import me.juangoncalves.mentra.network.CryptoCompareResponse
import me.juangoncalves.mentra.network.CryptoCompareService
import me.juangoncalves.mentra.network.models.CoinListSchema
import me.juangoncalves.mentra.network.models.CoinSchema
import me.juangoncalves.mentra.network.models.PriceSchema
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

class CoinRemoteDataSourceImplTest {

    @MockK lateinit var apiService: CryptoCompareService
    @MockK lateinit var logger: Logger

    private lateinit var sut: CoinRemoteDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = CoinRemoteDataSourceImpl(apiService, CoinMapper(), logger)
    }

    @Test
    fun `fetchCoins should return the list of coins that are not invalid or sponsored if the response is successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter<CoinListSchema>(coinListSchemaMoshiType)
            val coinListSchema = adapter.fromJson(fixture("/coin_list.json"))!!
            coEvery { apiService.listCoins() } returns Response.success(coinListSchema)

            // Act
            val result = sut.fetchCoins()

            // Assert
            assertEquals(result, listOf(Bitcoin, Ethereum, Ripple))
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response is not successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter<CoinListSchema>(coinListSchemaMoshiType)
            val coinListSchema = adapter.fromJson(fixture("/coin_list_error.json"))!!
            coEvery { apiService.listCoins() } returns Response.success(coinListSchema)

            // Act
            sut.fetchCoins()

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response body is null`() = runBlocking {
        // Arrange
        coEvery { apiService.listCoins() } returns Response.success(null)

        // Act
        sut.fetchCoins()

        // Assert
        Unit
    }

    @Test(expected = InternetConnectionException::class)
    fun `fetchCoins should throw a InternetConnectionException if the communication with the remote source fails`() =
        runBlocking {
            // Arrange
            coEvery { apiService.listCoins() } throws UnknownHostException()

            // Act
            sut.fetchCoins()

            // Assert
            Unit
        }

    @Test
    fun `fetchCoinPrice should return the coin price in USD from the api service if the response is successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter(PriceSchema::class.java)
            val price = adapter.fromJson(fixture("/btc_price.json"))!!
            coEvery { apiService.getCoinPrice(any()) } returns Response.success(price)

            // Act
            val result = sut.fetchCoinPrice(Bitcoin)

            // Assert
            coVerify { apiService.getCoinPrice(Bitcoin.symbol) }
            assertEquals(result.currency, Currency.USD)
            assertThat(result.value, closeTo(price.USD, 0.0001))
        }

    @Test(expected = InternetConnectionException::class)
    fun `fetchCoinPrice should throw a InternetConnectionException if the communication with the remote source fails`() =
        runBlocking {
            // Arrange
            coEvery { apiService.getCoinPrice(any()) } throws UnknownHostException()

            // Act
            sut.fetchCoinPrice(Ethereum)

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoinPrice should throw a ServerException if the response body is null`() =
        runBlocking {
            // Arrange
            coEvery { apiService.getCoinPrice(any()) } returns Response.success(null)

            // Act
            sut.fetchCoinPrice(Bitcoin)

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoinPrice should throw a ServerException if the response is not successful`() =
        runBlocking {
            // Arrange
            coEvery { apiService.getCoinPrice(any()) } returns Response.error(500, mockk())

            // Act
            sut.fetchCoinPrice(Bitcoin)

            // Assert
            Unit
        }


    // Helpers

    private val coinListSchemaMoshiType = Types.newParameterizedType(
        CryptoCompareResponse::class.java,
        Types.newParameterizedType(Map::class.java, String::class.java, CoinSchema::class.java)
    )
}