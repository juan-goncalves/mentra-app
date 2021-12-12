package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.ignoringFailure
import me.juangoncalves.mentra.domain_layer.errors.runCatching
import me.juangoncalves.mentra.domain_layer.extensions.elapsedDays
import me.juangoncalves.mentra.domain_layer.extensions.toPrice
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CurrencyRepository
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor(
    private val remoteSource: CurrencyRemoteDataSource,
    private val localSource: CurrencyLocalDataSource,
    private val errorHandler: ErrorHandler
) : CurrencyRepository {

    override suspend fun exchange(price: Price, target: Currency): Either<Failure, Price?> =
        errorHandler.runCatching {
            if (price.currency == target) {
                return@runCatching price
            }

            val cachedRate = ignoringFailure { localSource.getExchangeRate(price.currency, target) }

            val exchangeRate = if (cachedRate != null && cachedRate.timestamp.elapsedDays() <= 7) {
                cachedRate
            } else {
                val rates = remoteSource.fetchExchangeRates(price.currency).valuesToPrices()
                ignoringFailure {
                    localSource.saveExchangeRates(price.currency, rates.values.toList())
                }
                rates[target]
            }

            if (exchangeRate == null) {
                exchangeRate
            } else {
                val convertedValue = price.value * exchangeRate.value
                Price(convertedValue, exchangeRate.currency, price.timestamp)
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

    private fun Map<Currency, BigDecimal>.valuesToPrices(): Map<Currency, Price> =
        mapValues { (currency, value) ->
            value.toPrice(currency = currency)
        }

}