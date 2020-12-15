package me.juangoncalves.mentra.data.sources.currency

import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*

interface CurrencyLocalDataSource {

    suspend fun getCurrencies(): Set<Currency>

    suspend fun saveCurrencies(currencies: List<Currency>)

    suspend fun getExchangeRate(from: Currency, to: Currency): Price?

    suspend fun saveExchangeRates(base: Currency, rates: List<Price>)

}