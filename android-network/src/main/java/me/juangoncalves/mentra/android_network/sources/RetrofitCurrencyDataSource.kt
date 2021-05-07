package me.juangoncalves.mentra.android_network.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.android_network.error.ExchangeRatesApiException
import me.juangoncalves.mentra.android_network.services.ExchangeRateService
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class RetrofitCurrencyDataSource @Inject constructor(
    private val exchangeRateService: ExchangeRateService
) : CurrencyRemoteDataSource {

    override suspend fun fetchCurrencies(): Set<Currency> = withContext(Dispatchers.Default) {
        val exchangeRatesSchema = exchangeRateService.getExchangeRates("USD")

        if (!exchangeRatesSchema.wasSuccessful) {
            throw ExchangeRatesApiException(
                code = exchangeRatesSchema.error.code,
                message = exchangeRatesSchema.error.message,
            )
        }

        exchangeRatesSchema.rates.keys
            .mapNotNull { currencyCode ->
                try {
                    Currency.getInstance(currencyCode)
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()
    }

    override suspend fun fetchExchangeRates(base: Currency): Map<Currency, BigDecimal> =
        withContext(Dispatchers.Default) {
            val exchangeRatesSchema = exchangeRateService.getExchangeRates(base.currencyCode)

            if (!exchangeRatesSchema.wasSuccessful) {
                throw ExchangeRatesApiException(
                    code = exchangeRatesSchema.error.code,
                    message = exchangeRatesSchema.error.message,
                )
            }

            exchangeRatesSchema.rates
                .mapKeys { entry ->
                    ensureActive()
                    Currency.getInstance(entry.key)
                }
                .mapValues { entry ->
                    ensureActive()
                    entry.value.toBigDecimal()
                }
        }

}