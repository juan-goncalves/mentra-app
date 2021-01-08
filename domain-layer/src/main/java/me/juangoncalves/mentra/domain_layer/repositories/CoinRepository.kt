package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price


interface CoinRepository {

    /**
     * Latest available price per used coin.
     * A coin is considered to be in use if there's a wallet for it.
     */
    val pricesOfCoinsInUse: Flow<Map<Coin, Price>>

    suspend fun getCoins(forceNonCached: Boolean = false): Either<Failure, List<Coin>>

    /**
     * Obtains and caches the latest USD price of [coin], requesting it from the network if there isn't
     * a recently cached one.
     */
    suspend fun getCoinPrice(coin: Coin): Either<Failure, Price>

    /**
     * Obtains and caches the latest USD price of every coin in [coins], requesting it from the network if there isn't
     * a recently cached one.
     */
    suspend fun getCoinPrices(coins: List<Coin>): Either<Failure, Map<Coin, Price>>

    suspend fun updateCoin(coin: Coin): Either<Failure, Unit>

}
