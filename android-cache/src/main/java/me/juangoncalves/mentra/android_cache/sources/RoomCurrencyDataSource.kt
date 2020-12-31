package me.juangoncalves.mentra.android_cache.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.android_cache.daos.CurrencyDao
import me.juangoncalves.mentra.android_cache.models.CurrencyEntity
import me.juangoncalves.mentra.android_cache.models.ExchangeRateEntity
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*
import javax.inject.Inject

class RoomCurrencyDataSource @Inject constructor(
    private val currencyDao: CurrencyDao
) : CurrencyLocalDataSource {

    override suspend fun getCurrencies(): Set<Currency> = withContext(Dispatchers.Default) {
        currencyDao.getCurrencies()
            .map { record -> record.currency }
            .toHashSet()
    }

    override suspend fun saveCurrencies(currencies: List<Currency>) =
        withContext(Dispatchers.Default) {
            val entities = currencies.map { currency -> CurrencyEntity(currency) }
            currencyDao.insertCurrencies(*entities.toTypedArray())
        }

    override suspend fun getExchangeRate(from: Currency, to: Currency): Price? =
        withContext(Dispatchers.Default) {
            val record = currencyDao.getExchangeRate(from.currencyCode, to.currencyCode)
            if (record != null) Price(record.rate, record.target, record.timestamp) else null
        }

    override suspend fun saveExchangeRates(base: Currency, rates: List<Price>) =
        withContext(Dispatchers.Default) {
            val entities = rates.map { ExchangeRateEntity(base, it.currency, it.value) }
            currencyDao.insertExchangeRates(*entities.toTypedArray())
        }

}