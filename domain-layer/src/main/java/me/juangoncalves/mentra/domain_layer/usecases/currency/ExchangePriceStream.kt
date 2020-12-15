package me.juangoncalves.mentra.domain_layer.usecases.currency

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import javax.inject.Inject


class ExchangePriceStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val exchangePriceToPreferredCurrency: ExchangePriceToPreferredCurrency
) {

    @JvmName("exchangeAllWhenPreferredCurrencyChanges")
    fun Flow<List<Price>>.exchangeWhenPreferredCurrencyChanges(): Flow<List<Price>> =
        combine(preferenceRepository.currencyStream) { prices, _ ->
            prices.map { price ->
                exchangePriceToPreferredCurrency.execute(price)
            }
        }

    fun Flow<Price>.exchangeWhenPreferredCurrencyChanges(): Flow<Price> =
        combine(preferenceRepository.currencyStream) { price, _ ->
            exchangePriceToPreferredCurrency.execute(price)
        }

}