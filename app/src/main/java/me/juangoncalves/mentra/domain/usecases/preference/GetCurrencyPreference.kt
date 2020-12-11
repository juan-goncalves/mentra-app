package me.juangoncalves.mentra.domain.usecases.preference

import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain.repositories.PreferenceRepository
import java.util.*
import javax.inject.Inject

class GetCurrencyPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend fun execute(): Currency {
        return preferenceRepository.currencyStream.first()
    }

}
