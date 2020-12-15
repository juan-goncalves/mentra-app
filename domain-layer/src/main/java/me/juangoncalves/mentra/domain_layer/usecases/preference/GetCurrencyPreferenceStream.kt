package me.juangoncalves.mentra.domain_layer.usecases.preference

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import java.util.*
import javax.inject.Inject

class GetCurrencyPreferenceStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    operator fun invoke(): Flow<Currency> = preferenceRepository.currencyStream

}
