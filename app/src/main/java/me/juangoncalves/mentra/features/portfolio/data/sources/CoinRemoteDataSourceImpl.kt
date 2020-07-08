package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.errors.InternetConnectionException
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.extensions.TAG
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.core.network.CryptoCompareResponse.State
import me.juangoncalves.mentra.core.network.CryptoCompareService
import me.juangoncalves.mentra.core.network.schemas.CoinSchema
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import java.time.LocalDateTime

class CoinRemoteDataSourceImpl(
    private val apiService: CryptoCompareService,
    private val logger: Logger
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
                .map { it.toDomain() }
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
        return Price(Currency.USD, priceSchema.USD, LocalDateTime.now())
    }

    private fun buildImageUrl(baseUrl: String): (CoinSchema) -> CoinSchema {
        return { schema: CoinSchema ->
            schema.copy(imageUrl = baseUrl + schema.imageUrl)
        }
    }

    // TODO: Move to a mapper class
    private fun CoinSchema.toDomain(): Coin {
        if (name.isEmpty() || imageUrl.isEmpty() || symbol.isEmpty()) {
            return Coin.Invalid
        }
        return Coin(name, symbol, imageUrl)
    }

}