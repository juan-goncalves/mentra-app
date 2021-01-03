package me.juangoncalves.mentra.domain_layer.usecases.currency

import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CurrencyRepository
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetCurrencyPreference
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
        val currency = getCurrencyPreference().rightValue ?: Currency.getInstance("USD")
        return currencyRepository.exchange(price, currency).rightValue ?: price
    }

}