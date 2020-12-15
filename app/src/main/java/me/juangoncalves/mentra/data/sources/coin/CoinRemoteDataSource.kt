package me.juangoncalves.mentra.data.sources.coin

import me.juangoncalves.mentra.domain_layer.errors.InternetConnectionException
import me.juangoncalves.mentra.domain_layer.errors.ServerException
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price

interface CoinRemoteDataSource {

    /**
     * Fetches the list of supported coins from a remote data source like a server or a
     * third party API.
     *
     * @throws ServerException for all error codes.
     * @throws InternetConnectionException if the communication with the remote source fails.
     */
    suspend fun fetchCoins(): List<Coin>

    /**
     * Fetches the current price in USD of a single coin from a remote data source like a server
     * or a third party API.
     *
     * @throws ServerException for all error codes.
     * @throws InternetConnectionException if the communication with the remote source fails.
     */
    suspend fun fetchCoinPrice(coin: Coin): Price

}