package me.juangoncalves.mentra.domain.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin

interface IconRepository {

    suspend fun getAlternativeIconFor(coin: Coin): Either<Failure, String?>

}