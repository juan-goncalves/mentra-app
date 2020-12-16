package me.juangoncalves.mentra.android_network.sources

import me.juangoncalves.mentra.android_network.mapper.CoinMapper
import me.juangoncalves.mentra.android_network.models.CoinSchema
import me.juangoncalves.mentra.android_network.models.CryptoCompareResponse.State
import me.juangoncalves.mentra.android_network.services.CryptoCompareService
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.InternetConnectionException
import me.juangoncalves.mentra.domain_layer.errors.ServerException
import me.juangoncalves.mentra.domain_layer.extensions.TAG
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class RetrofitCoinDataSource @Inject constructor(
    private val apiService: CryptoCompareService,
    private val coinMapper: CoinMapper,
    private val logger: MentraLogger
) : CoinRemoteDataSource {

    override suspend fun fetchCoins(): List<Coin> {
        val response = try {
            apiService.listCoins()
        } catch (e: Exception) {
            logger.error(TAG, "Exception while trying to fetch coin list:\n$e")
            throw InternetConnectionException()
        }
        val resource = response.body() ?: throw ServerException("Response body was null")
        return when (resource.status) {
            State.Error -> throw ServerException("Server error: ${resource.message}")
            State.Success -> resource.data.values
                .filterNot { it.sponsored }
                .map(buildImageUrl(resource.baseImageUrl))
                .map(coinMapper::map)
                .filterNot { it == Coin.Invalid }
        }
    }

    override suspend fun fetchCoinPrice(coin: Coin): Price {
        val response = try {
            apiService.getCoinPrice(coin.symbol)
        } catch (e: Exception) {
            throw InternetConnectionException()
        }

        val priceSchema = response.body() ?: throw ServerException("Response body was null")

        return Price(BigDecimal(priceSchema.USD), Currency.getInstance("USD"), LocalDateTime.now())
    }

    private fun buildImageUrl(baseUrl: String): (CoinSchema) -> CoinSchema {
        return { schema: CoinSchema ->
            schema.copy(imageUrl = baseUrl + schema.imageUrl)
        }
    }

}
