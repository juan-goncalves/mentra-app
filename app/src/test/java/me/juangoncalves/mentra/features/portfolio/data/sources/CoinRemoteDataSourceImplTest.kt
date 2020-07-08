package me.juangoncalves.mentra.features.portfolio.data.sources

import com.squareup.moshi.Types
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.Bitcoin
import me.juangoncalves.mentra.Ethereum
import me.juangoncalves.mentra.core.errors.InternetConnectionException
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.portfolio.data.schemas.CoinListSchema
import me.juangoncalves.mentra.features.portfolio.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.portfolio.data.schemas.CryptoCompareResponse
import me.juangoncalves.mentra.features.portfolio.data.schemas.CryptoCompareResponse.State
import me.juangoncalves.mentra.features.portfolio.data.schemas.PriceSchema
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.fixture
import me.juangoncalves.mentra.moshi
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

    private lateinit var SUT: CoinRemoteDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        SUT = CoinRemoteDataSourceImpl(apiService, logger)
    }

    @Test
    fun `fetchCoins should return the list of coins when the response is successful`() =
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
            setupMockListCoinsApiFailure()

            // Act
            SUT.fetchCoins()

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response body is null`() = runBlocking {
        // Arrange
        setupMockListCoinsSuccessWithNullBody()

        // Act
        SUT.fetchCoins()

        // Assert
        Unit
    }

    @Test(expected = InternetConnectionException::class)
    fun `fetchCoins should throw a InternetConnectionException if the api service call throws an exception`() =
        runBlocking {
            // Arrange
            setupMockListCoinsNetworkError()

            // Act
            SUT.fetchCoins()

            // Assert
            Unit
        }

    @Test
    fun `fetchCoinPrice should return the coin price in USD from the API service when the response is successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter(PriceSchema::class.java)
            val price = adapter.fromJson(fixture("/btc_price.json"))!!
            val apiResource = CryptoCompareResponse(State.Success, data = price)
            coEvery { apiService.getCoinPrice(any()) } returns Response.success(apiResource)

            // Act
            val result = SUT.fetchCoinPrice(Bitcoin)

            // Assert
            coVerify { apiService.getCoinPrice(Bitcoin.symbol) }
            assertEquals(result.currency, Currency.USD)
            assertThat(result.value, closeTo(price.USD, 0.0001))
        }

    @Test(expected = ServerException::class)
    fun `fetchCoinPrice should throw a ServerException if the response is not successful`() =
        runBlocking {
            // Arrange
            coEvery { apiService.getCoinPrice(any()) } returns Response.error(500, mockk())

            // Act
            SUT.fetchCoinPrice(Ethereum)

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoinPrice should throw a ServerException if the response body is null`() =
        runBlocking {
            // Arrange
            coEvery { apiService.getCoinPrice(any()) } returns Response.success(null)

            // Act
            SUT.fetchCoinPrice(Bitcoin)

            // Assert
            Unit
        }

//    @Test
//    fun `retrofit playground`() = runBlocking {
//        /// Retrofit playground
//        val retro = Retrofit.Builder()
//            .baseUrl("https://jsonplaceholder.typicode.com/")
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//
//        val service = retro.create(CryptoCompareService::class.java)
//        val res = service.getCoinPrice("")
//        println(res)
//        Unit
//    }

    private fun setupMockListCoinsSuccess(): CoinListSchema {
        val coinListResponseType = Types.newParameterizedType(
            CryptoCompareResponse::class.java,
            Types.newParameterizedType(Map::class.java, String::class.java, CoinSchema::class.java)
        )
        val adapter = moshi.adapter<CoinListSchema>(coinListResponseType)
        val coinListSchema = adapter.fromJson(fixture("/coin_list.json"))!!
        coEvery { apiService.listCoins() } returns Response.success(coinListSchema)
        return coinListSchema
    }

    private fun setupMockListCoinsSuccessWithNullBody() {
        coEvery { apiService.listCoins() } returns Response.success(null)
    }

    private fun setupMockListCoinsApiFailure() {
        val coinListResponseType = Types.newParameterizedType(
            CryptoCompareResponse::class.java,
            Types.newParameterizedType(Map::class.java, String::class.java, CoinSchema::class.java)
        )
        val adapter = moshi.adapter<CoinListSchema>(coinListResponseType)
        val coinListSchema = adapter.fromJson(fixture("/coin_list_error.json"))!!
        coEvery { apiService.listCoins() } returns Response.success(coinListSchema)
    }

    private fun setupMockListCoinsNetworkError() {
        coEvery { apiService.listCoins() } throws UnknownHostException()
    }
}