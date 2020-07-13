package me.juangoncalves.mentra.domain.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price

interface CoinRepository {

    suspend fun getCoins(): Either<Failure, List<Coin>>

    suspend fun getCoinPrice(coin: Coin, currency: Currency): Either<Failure, Price>

}
