package me.juangoncalves.mentra.android_network.sources

import com.squareup.moshi.Types
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import me.juangoncalves.mentra.android_network.*
import me.juangoncalves.mentra.android_network.error.CryptoCompareResponseException
import me.juangoncalves.mentra.android_network.mapper.CoinMapper
import me.juangoncalves.mentra.android_network.services.crypto_compare.CryptoCompareApi
import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CoinListSchema
import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CoinSchema
import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CryptoCompareResponse
import me.juangoncalves.mentra.android_network.services.crypto_compare.models.PriceSchema
import me.juangoncalves.mentra.test_utils.shouldBe
import me.juangoncalves.mentra.test_utils.shouldBeCloseTo
import org.junit.Before
import org.junit.Test

class RetrofitCoinDataSourceTest {

    //region Rules
    //endregion

    //region Mocks
    @MockK lateinit var apiServiceMock: CryptoCompareApi
    //endregion

    private lateinit var sut: RetrofitCoinDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = RetrofitCoinDataSource(apiServiceMock, CoinMapper())
    }

    @Test
    fun `fetchCoins should return the list of coins that are not invalid or sponsored if the response is successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter<CoinListSchema>(coinListSchemaMoshiType)
            val coinListSchema = adapter.fromJson(fixture("/coin_list.json"))!!
            coEvery { apiServiceMock.listCoins() } returns coinListSchema

            // Act
            val result = sut.fetchCoins()

            // Assert
            result shouldBe listOf(Bitcoin, Ethereum, Ripple)
        }

    @Test(expected = CryptoCompareResponseException::class)
    fun `fetchCoins should throw a CryptoCompareResponseException if the response is not successful`() =
        runBlocking {
            // Arrange
            val adapter = moshi.adapter<CoinListSchema>(coinListSchemaMoshiType)
            val coinListSchema = adapter.fromJson(fixture("/coin_list_error.json"))!!
            coEvery { apiServiceMock.listCoins() } returns coinListSchema

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
            coEvery { apiServiceMock.getCoinPrice(any()) } returns price

            // Act
            val result = sut.fetchCoinPrice(Bitcoin)

            // Assert
            coVerify { apiServiceMock.getCoinPrice(Bitcoin.symbol) }
            result.currency shouldBe USD
            result.value shouldBeCloseTo price.USD.toBigDecimal()
        }


    //region Helpers
    private val coinListSchemaMoshiType = Types.newParameterizedType(
        CryptoCompareResponse::class.java,
        Types.newParameterizedType(Map::class.java, String::class.java, CoinSchema::class.java)
    )
    //endregion

}