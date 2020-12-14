package me.juangoncalves.mentra.domain.usecases.currency

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.repositories.CurrencyRepository
import java.util.*
import javax.inject.Inject

class GetSupportedCurrencies @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    suspend fun execute(): Either<Failure, Set<Currency>> {
        return currencyRepository.getSupportedCurrencies()
    }

}