package me.juangoncalves.mentra.domain.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.FetchPriceFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price

interface CoinRepository {

    /**
     * Latest available price per used coin.
     * A coin is considered to be in use if there's a wallet for it.
     */
    val pricesOfCoinsInUse: Flow<Map<Coin, Price>>

    suspend fun getCoins(): Either<Failure, List<Coin>>

    /**
     * Obtains and caches the latest price of [coin] in USD, requesting it from the network if there isn't
     * a locally available one from the last 5 minutes. If the network fetch fails, a [FetchPriceFailure]
     * with the most recent cached coin price will be returned.
     */
    suspend fun getCoinPrice(coin: Coin): Either<Failure, Price>

    suspend fun updateCoin(coin: Coin): Either<Failure, Unit>

}
