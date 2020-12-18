package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data_layer.extensions.elapsedDays
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.*
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toPrice
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CurrencyRepository
import java.util.*
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor(
    private val remoteSource: CurrencyRemoteDataSource,
    private val localSource: CurrencyLocalDataSource,
    private val errorHandler: ErrorHandler
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

    override suspend fun getCurrencies(): Either<Failure, Set<Currency>> =
        errorHandler.runCatching(Dispatchers.Default) {
            val cachedCurrencies = ignoringFailure { localSource.getCurrencies() }
            if (cachedCurrencies != null && cachedCurrencies.isNotEmpty()) {
                cachedCurrencies
            } else {
                val remoteCurrencies = remoteSource.fetchCurrencies()
                ignoringFailure { localSource.saveCurrencies(remoteCurrencies.toList()) }
                remoteCurrencies
            }
        }

    private suspend fun getCachedExchangeRate(price: Price, target: Currency): Price {
        return try {
            localSource.getExchangeRate(price.currency, target) ?: Price.None
        } catch (e: Exception) {
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
            Price.None
        }
    }

}