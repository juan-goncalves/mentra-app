package me.juangoncalves.mentra.data.sources.currency

import java.math.BigDecimal
import java.util.*

interface CurrencyRemoteDataSource {

    suspend fun fetchCurrencies(): Set<Currency>

    suspend fun fetchExchangeRates(base: Currency): Map<Currency, BigDecimal>

}