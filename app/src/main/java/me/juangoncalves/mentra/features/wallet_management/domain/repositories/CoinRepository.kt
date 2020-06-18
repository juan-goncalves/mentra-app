package me.juangoncalves.mentra.features.wallet_management.domain.repositories

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency

interface CoinRepository {

    suspend fun getCoins(): Either<Failure, List<Coin>>

    suspend fun getCoinPrices(
        coins: List<Coin>,
        currency: Currency
    ): Either<Failure, Map<Coin, Double>>

}