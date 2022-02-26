package me.juangoncalves.mentra.android_network.sources

import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.android_network.error.CryptoCompareResponseException
import me.juangoncalves.mentra.android_network.mapper.CoinMapper
import me.juangoncalves.mentra.android_network.services.crypto_compare.CryptoCompareApi
import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CryptoCompareResponse.State
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.domain_layer.extensions.now
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

private const val CryptoCompareImageUrl = "https://www.cryptocompare.com"

class RetrofitCoinDataSource @Inject constructor(
    private val apiService: CryptoCompareApi,
    private val coinMapper: CoinMapper
) : CoinRemoteDataSource {

    override suspend fun fetchCoins(): List<Coin> {
        val resource = apiService.listCoins()

        return when (resource.status) {
            State.Error -> throw CryptoCompareResponseException("CryptoCompare error response with code: ${resource.status}")
            State.Success -> resource.data.values
                .filterNot { it.sponsored }
                .map { schema -> schema.copy(imageUrl = CryptoCompareImageUrl + schema.imageUrl) }
                .map(coinMapper::map)
                .filterNot { it == Coin.Invalid }
        }
    }

    override suspend fun fetchCoinPrice(coin: Coin): Price {
        val schema = apiService.getCoinPrice(coin.symbol)

        return Price(
            schema.USD.toBigDecimal(),
            Currency.getInstance("USD"),
            LocalDateTime.now()
        )
    }
}
