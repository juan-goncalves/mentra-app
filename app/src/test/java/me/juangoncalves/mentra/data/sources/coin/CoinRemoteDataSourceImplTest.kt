package me.juangoncalves.mentra.data.sources.coin

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
import me.juangoncalves.mentra.log.Logger
import me.juangoncalves.mentra.network.CryptoCompareResponse
import me.juangoncalves.mentra.network.CryptoCompareService
import me.juangoncalves.mentra.network.models.CoinListSchema
import me.juangoncalves.mentra.network.models.CoinSchema
import me.juangoncalves.mentra.network.models.PriceSchema
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

class CoinRemoteDataSourceImplTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var apiServiceMock: CryptoCompareService
    @MockK lateinit var loggerMock: Logger
    //endregion

    private lateinit var sut: CoinRemoteDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = CoinRemoteDataSourceImpl(
            apiServiceMock,
            CoinMapper(),
            loggerMock
        )
    }

    @Test
    fun `fetchCoins should return the list of coins that are not invalid or sponsored if the response is successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter<CoinListSchema>(coinListSchemaMoshiType)
            val coinListSchema = adapter.fromJson(fixture("/coin_list.json"))!!
            coEvery { apiServiceMock.listCoins() } returns Response.success(coinListSchema)

            // Act
            val result = sut.fetchCoins()

            // Assert
            result shouldBe listOf(Bitcoin, Ethereum, Ripple)
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response is not successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter<CoinListSchema>(coinListSchemaMoshiType)
            val coinListSchema = adapter.fromJson(fixture("/coin_list_error.json"))!!
            coEvery { apiServiceMock.listCoins() } returns Response.success(coinListSchema)

            // Act
            sut.fetchCoins()

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoins should throw a ServerException if the response body is null`() = runBlocking {
        // Arrange
        coEvery { apiServiceMock.listCoins() } returns Response.success(null)

        // Act
        sut.fetchCoins()

        // Assert
        Unit
    }

    @Test(expected = InternetConnectionException::class)
    fun `fetchCoins should throw a InternetConnectionException if the communication with the remote source fails`() =
        runBlocking {
            // Arrange
            coEvery { apiServiceMock.listCoins() } throws UnknownHostException()

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
            coEvery { apiServiceMock.getCoinPrice(any()) } returns Response.success(price)

            // Act
            val result = sut.fetchCoinPrice(Bitcoin)

            // Assert
            coVerify { apiServiceMock.getCoinPrice(Bitcoin.symbol) }
            result.currency shouldBe USD
            result.value shouldBeCloseTo price.USD.toBigDecimal()
        }

    @Test(expected = InternetConnectionException::class)
    fun `fetchCoinPrice should throw a InternetConnectionException if the communication with the remote source fails`() =
        runBlocking {
            // Arrange
            coEvery { apiServiceMock.getCoinPrice(any()) } throws UnknownHostException()

            // Act
            sut.fetchCoinPrice(Ethereum)

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoinPrice should throw a ServerException if the response body is null`() =
        runBlocking {
            // Arrange
            coEvery { apiServiceMock.getCoinPrice(any()) } returns Response.success(null)

            // Act
            sut.fetchCoinPrice(Bitcoin)

            // Assert
            Unit
        }

    @Test(expected = ServerException::class)
    fun `fetchCoinPrice should throw a ServerException if the response is not successful`() =
        runBlocking {
            // Arrange
            coEvery { apiServiceMock.getCoinPrice(any()) } returns Response.error(500, mockk())

            // Act
            sut.fetchCoinPrice(Bitcoin)

            // Assert
            Unit
        }

    //region Helpers
    private val coinListSchemaMoshiType = Types.newParameterizedType(
        CryptoCompareResponse::class.java,
        Types.newParameterizedType(Map::class.java, String::class.java, CoinSchema::class.java)
    )
    //endregion

}