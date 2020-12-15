package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*

interface CurrencyRepository {

    suspend fun getSupportedCurrencies(): Either<Failure, Set<Currency>>

    suspend fun exchange(price: Price, target: Currency): Either<Failure, Price>

}