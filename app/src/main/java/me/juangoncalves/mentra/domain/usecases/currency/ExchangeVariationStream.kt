package me.juangoncalves.mentra.domain.usecases.currency

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.juangoncalves.mentra.domain.models.ValueVariation
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import javax.inject.Inject

class ExchangeVariationStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val exchangePriceToPreferredCurrency: ExchangePriceToPreferredCurrency
) {

    fun Flow<ValueVariation>.exchangeWhenPreferredCurrencyChanges(): Flow<ValueVariation> =
        combine(preferenceRepository.currencyStream) { variation, _ ->
            val exchangedVariation = exchangePriceToPreferredCurrency.execute(variation.difference)
            variation.copy(difference = exchangedVariation)
        }

}