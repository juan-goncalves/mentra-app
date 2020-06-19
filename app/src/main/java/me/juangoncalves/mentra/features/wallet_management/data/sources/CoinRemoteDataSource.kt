package me.juangoncalves.mentra.features.wallet_management.data.sources

import me.juangoncalves.mentra.core.errors.NotFoundException
import me.juangoncalves.mentra.core.errors.ServerException
import me.juangoncalves.mentra.features.wallet_management.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Money

interface CoinRemoteDataSource {

    /**
     * Fetches the list of supported coins from a remote data source like a server or a
     * third party API.
     *
     * @throws ServerException for all error codes.
     */
    suspend fun fetchCoins(): List<CoinSchema>

    /**
     * Fetches the current price of a single coin from a remote data source like a server or a
     * third party API.
     *
     * @throws NotFoundException when the coin price is not available in the remote source.
     * @throws ServerException for all error codes.
     */
    suspend fun fetchCoinPrice(coin: Coin): Money

}