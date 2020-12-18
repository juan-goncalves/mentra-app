package me.juangoncalves.mentra.domain_layer.usecases.currency

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.repositories.CurrencyRepository
import java.util.*
import javax.inject.Inject

class GetSupportedCurrencies @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    suspend fun execute(): Either<OldFailure, Set<Currency>> {
        return currencyRepository.getSupportedCurrencies()
    }

}