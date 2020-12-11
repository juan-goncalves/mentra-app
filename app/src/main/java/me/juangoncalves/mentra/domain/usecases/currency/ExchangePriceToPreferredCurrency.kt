package me.juangoncalves.mentra.domain.usecases.currency

import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CurrencyRepository
import me.juangoncalves.mentra.domain.usecases.preference.GetCurrencyPreference
import me.juangoncalves.mentra.extensions.rightValue
import java.util.*
import javax.inject.Inject

class ExchangePriceToPreferredCurrency @Inject constructor(
    private val getCurrencyPreference: GetCurrencyPreference,
    private val currencyRepository: CurrencyRepository
) {

    /**
     * Converts the [price] value into the user's preferred [Currency].
     * If the conversion fails, it returns the received [price].
     */
    suspend fun execute(price: Price): Price {
        val currency = getCurrencyPreference.execute()
        return currencyRepository.exchange(price, currency).rightValue ?: price
    }

}