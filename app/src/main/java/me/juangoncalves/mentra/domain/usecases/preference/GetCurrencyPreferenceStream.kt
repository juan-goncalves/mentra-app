package me.juangoncalves.mentra.domain.usecases.preference

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import java.util.*
import javax.inject.Inject

class GetCurrencyPreferenceStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    operator fun invoke(): Flow<Currency> = preferenceRepository.currencyStream

}
