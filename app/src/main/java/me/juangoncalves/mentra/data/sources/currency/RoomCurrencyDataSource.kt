package me.juangoncalves.mentra.data.sources.currency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.db.daos.CurrencyDao
import me.juangoncalves.mentra.db.models.CurrencyEntity
import me.juangoncalves.mentra.db.models.ExchangeRateEntity
import me.juangoncalves.mentra.domain.models.Price
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

    override suspend fun getExchangeRate(from: Currency, to: Currency): Price? {
        val record = currencyDao.getExchangeRate(from.currencyCode, to.currencyCode)
            ?: return null

        return Price(record.rate, record.target, record.timestamp)
    }

    override suspend fun saveExchangeRates(base: Currency, rates: List<Price>) =
        withContext(Dispatchers.Default) {
            val entities = rates.map { price ->
                ExchangeRateEntity(base, price.currency, price.value)
            }

            currencyDao.insertExchangeRates(*entities.toTypedArray())
        }

}