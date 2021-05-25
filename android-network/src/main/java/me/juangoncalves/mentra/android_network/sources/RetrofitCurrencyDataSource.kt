package me.juangoncalves.mentra.android_network.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.android_network.error.CurrencyLayerApiException
import me.juangoncalves.mentra.android_network.services.currency_layer.CurrencyLayerApi
import me.juangoncalves.mentra.android_network.services.currency_layer.models.CurrencyListResponse
import me.juangoncalves.mentra.android_network.services.currency_layer.models.ExchangeRatesResponse
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class RetrofitCurrencyDataSource @Inject constructor(
    private val currenciesApi: CurrencyLayerApi
) : CurrencyRemoteDataSource {

    override suspend fun fetchCurrencies(): Set<Currency> = withContext(Dispatchers.Default) {
        val response = currenciesApi.getCurrencies()

        if (!response.wasSuccessful) {
            throw response.toException()
        }

        response.currencies
            .mapNotNull { (currencyCode, _) ->
                ensureActive()
                try {
                    val currency = Currency.getInstance(currencyCode)
                    val hasNumbers = currency.displayName.contains(Regex("[0-9]+"))
                    if (!hasNumbers) currency else null
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()
    }

    override suspend fun fetchExchangeRates(base: Currency): Map<Currency, BigDecimal> =
        withContext(Dispatchers.Default) {
            val response = currenciesApi.getExchangeRates(base.currencyCode)

            if (!response.wasSuccessful) {
                throw response.toException()
            }

            response.quotes
                .mapKeys { entry ->
                    ensureActive()
                    Currency.getInstance(entry.key.substring(3))
                }
                .mapValues { entry ->
                    ensureActive()
                    entry.value.toBigDecimal()
                }
        }


    private fun CurrencyListResponse.toException(): CurrencyLayerApiException {
        return CurrencyLayerApiException(
            error?.code ?: -1,
            error?.message ?: "",
        )
    }

    private fun ExchangeRatesResponse.toException(): CurrencyLayerApiException {
        return CurrencyLayerApiException(
            error?.code ?: -1,
            error?.message ?: "",
        )
    }
}