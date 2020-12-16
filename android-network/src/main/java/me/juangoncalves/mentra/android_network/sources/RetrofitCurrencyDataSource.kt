package me.juangoncalves.mentra.android_network.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.android_network.services.ExchangeRateService
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.InternetConnectionException
import me.juangoncalves.mentra.domain_layer.errors.ServerException
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class RetrofitCurrencyDataSource @Inject constructor(
    private val exchangeRateService: ExchangeRateService
) : CurrencyRemoteDataSource {

    override suspend fun fetchCurrencies(): Set<Currency> = withContext(Dispatchers.Default) {
        val response = try {
            exchangeRateService.getExchangeRates("USD")
        } catch (e: Exception) {
            throw InternetConnectionException()
        }

        val exchangeRatesSchema = response.body()
            ?: throw ServerException("Response body was null")

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
            val response = try {
                exchangeRateService.getExchangeRates(base.currencyCode)
            } catch (e: Exception) {
                throw InternetConnectionException()
            }

            val exchangeRatesSchema = response.body()
                ?: throw ServerException("Response body was null")

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