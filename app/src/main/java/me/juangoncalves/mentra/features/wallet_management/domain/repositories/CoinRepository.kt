package me.juangoncalves.mentra.features.wallet_management.domain.repositories

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin

interface CoinRepository {
    suspend fun getCoins(): Either<Failure, List<Coin>>
}