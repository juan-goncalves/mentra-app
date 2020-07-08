package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.core.errors.InternetConnectionException
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.core.network.schemas.CoinSchema
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

interface CoinRemoteDataSource {

    /**
     * Fetches the list of supported coins from a remote data source like a server or a
     * third party API.
     *
     * @throws ServerException for all error codes.
     * @throws InternetConnectionException if the communication with the remote source fails.
     */
    suspend fun fetchCoins(): List<CoinSchema>

    /**
     * Fetches the current price in USD of a single coin from a remote data source like a server
     * or a third party API.
     *
     * @throws ServerException for all error codes.
     * @throws InternetConnectionException if the communication with the remote source fails.
     */
    suspend fun fetchCoinPrice(coin: Coin): Price

}