package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Coin

interface IconRepository {

    suspend fun getAlternativeIconFor(coin: Coin): Either<OldFailure, String?>

}