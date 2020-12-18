package me.juangoncalves.mentra.domain_layer.usecases.currency

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.repositories.CurrencyRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidInteractor
import java.util.*
import javax.inject.Inject

class GetSupportedCurrencies @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : VoidInteractor<Set<Currency>> {

    override suspend fun invoke(): Either<Failure, Set<Currency>> {
        return currencyRepository.getCurrencies()
    }

}