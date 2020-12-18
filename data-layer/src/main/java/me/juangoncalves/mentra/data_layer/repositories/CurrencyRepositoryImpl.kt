package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data_layer.extensions.elapsedDays
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.CurrenciesNotAvailable
import me.juangoncalves.mentra.domain_layer.errors.ExchangeRateNotAvailable
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.TAG
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toPrice
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CurrencyRepository
import java.util.*
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor(
    private val remoteSource: CurrencyRemoteDataSource,
    private val localSource: CurrencyLocalDataSource,
    private val logger: MentraLogger
) : CurrencyRepository {

    override suspend fun exchange(price: Price, target: Currency): Either<OldFailure, Price> =
        withContext(Dispatchers.Default) {
            val cachedRate = getCachedExchangeRate(price, target)

            val exchangeRate = when {
                cachedRate != Price.None && cachedRate.timestamp.elapsedDays() <= 7 -> cachedRate
                else -> fetchExchangeRate(price.currency, target)
            }

            when (exchangeRate) {
                Price.None -> ExchangeRateNotAvailable().toLeft()
                else -> {
                    val convertedValue = price.value * exchangeRate.value
                    Price(convertedValue, exchangeRate.currency, price.timestamp).toRight()
                }
            }
        }

    override suspend fun getSupportedCurrencies(): Either<OldFailure, Set<Currency>> =
        withContext(Dispatchers.Default) {
            val cachedCurrencies = getCachedCurrencies()

            val supportedCurrencies = when {
                cachedCurrencies != null && cachedCurrencies.isNotEmpty() -> cachedCurrencies
                else -> fetchCurrencies()
            }

            when (supportedCurrencies) {
                null -> CurrenciesNotAvailable().toLeft()
                else -> supportedCurrencies.toRight()
            }
        }

    private suspend fun getCachedCurrencies(): Set<Currency>? {
        return try {
            localSource.getCurrencies()
        } catch (e: Exception) {
            logger.error(TAG, "Error while accessing the cached currencies.\n$e")
            null
        }
    }

    private suspend fun fetchCurrencies(): Set<Currency>? {
        return try {
            remoteSource.fetchCurrencies().also {
                localSource.saveCurrencies(it.toList())
            }
        } catch (e: Exception) {
            logger.error(TAG, "Error while fetching currencies.\n$e")
            null
        }
    }

    private suspend fun getCachedExchangeRate(price: Price, target: Currency): Price {
        return try {
            localSource.getExchangeRate(price.currency, target) ?: Price.None
        } catch (e: Exception) {
            logger.error(TAG, "Error while accessing a cached exchange rate.\n$e")
            Price.None
        }
    }

    private suspend fun fetchExchangeRate(base: Currency, target: Currency): Price {
        return try {
            val rates = remoteSource.fetchExchangeRates(base).mapValues { entry ->
                entry.value.toPrice(currency = entry.key)
            }
            localSource.saveExchangeRates(base, rates.values.toList())
            rates.getOrDefault(target, Price.None)
        } catch (e: Exception) {
            logger.error(TAG, "Error while fetching exchange rates.\n$e")
            Price.None
        }
    }

}