package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Coin

interface IconRepository {

    suspend fun getAlternativeIconFor(coin: Coin): Either<Failure, String?>

}