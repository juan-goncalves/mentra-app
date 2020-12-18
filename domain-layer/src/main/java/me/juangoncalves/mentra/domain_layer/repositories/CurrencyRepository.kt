package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*

interface CurrencyRepository {

    suspend fun getCurrencies(): Either<Failure, Set<Currency>>

    suspend fun exchange(price: Price, target: Currency): Either<OldFailure, Price>

}