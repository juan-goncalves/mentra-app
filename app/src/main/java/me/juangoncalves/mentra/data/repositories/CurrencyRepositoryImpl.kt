package me.juangoncalves.mentra.data.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.domain.errors.CurrenciesNotAvailable
import me.juangoncalves.mentra.domain.errors.ExchangeRateNotAvailable
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CurrencyRepository
import me.juangoncalves.mentra.extensions.*
import me.juangoncalves.mentra.log.Logger
import java.util.*
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor(
    private val remoteSource: CurrencyRemoteDataSource,
    private val localSource: CurrencyLocalDataSource,
    private val logger: Logger
) : CurrencyRepository {

    override suspend fun exchange(price: Price, target: Currency): Either<Failure, Price> =
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
                    Price(convertedValue, exchangeRate.currency, exchangeRate.timestamp).toRight()
                }
            }
        }

    override suspend fun getSupportedCurrencies(): Either<Failure, Set<Currency>> =
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