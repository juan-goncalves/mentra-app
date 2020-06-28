package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.extensions.TAG
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.portfolio.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

class CoinRemoteDataSourceImpl(
    private val apiService: CryptoCompareService,
    private val logger: Logger
) : CoinRemoteDataSource {

    override suspend fun fetchCoins(): List<CoinSchema> {
        try {
            val response = apiService.listCoins()
            if (response.isSuccessful) {
                val body = response.body() ?: throw ServerException("Response body was null")
                return body.data.values.map {
                    it.copy(imageUrl = body.baseImageUrl + it.imageUrl)
                }
            } else {
                throw ServerException()
            }
        } catch (e: Exception) {
            logger.error(TAG, "Exception while trying to fetch coin list:\n$e")
            throw ServerException("${e.javaClass} thrown while trying to fetch coin list")
        }
    }

    override suspend fun fetchCoinPrice(coin: Coin, currency: Currency): Price {
        TODO("not implemented")
    }

}
