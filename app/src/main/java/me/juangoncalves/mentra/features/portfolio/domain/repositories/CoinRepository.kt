package me.juangoncalves.mentra.features.portfolio.domain.repositories

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

interface CoinRepository {

    suspend fun getCoins(): Either<Failure, List<Coin>>

    suspend fun getCoinPrice(coin: Coin, currency: Currency): Either<Failure, Price>

}
