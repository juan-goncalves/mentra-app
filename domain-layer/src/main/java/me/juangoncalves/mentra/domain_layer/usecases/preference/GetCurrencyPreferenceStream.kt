package me.juangoncalves.mentra.domain_layer.usecases.preference

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.FlowUseCase
import java.util.*
import javax.inject.Inject

class GetCurrencyPreferenceStream @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : FlowUseCase<Currency> {

    override operator fun invoke(): Flow<Currency> = preferenceRepository.currencyStream

}
