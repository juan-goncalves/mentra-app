package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.FetchPriceFailure
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
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
     * Obtains and caches the latest price of [coin] in USD, requesting it from the network if there isn't
     * a locally available one from the last 5 minutes. If the network fetch fails, a [FetchPriceFailure]
     * with the most recent cached coin price will be returned.
     */
    suspend fun getCoinPrice(coin: Coin): Either<Failure, Price>

    suspend fun updateCoin(coin: Coin): Either<OldFailure, Unit>

}
