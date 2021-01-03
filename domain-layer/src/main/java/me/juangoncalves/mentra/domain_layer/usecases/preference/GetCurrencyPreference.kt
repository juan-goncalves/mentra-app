package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import kotlinx.coroutines.flow.first
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidUseCase
import java.util.*
import javax.inject.Inject

class GetCurrencyPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : VoidUseCase<Currency> {

    override suspend fun invoke(): Either<Failure, Currency> {
        return preferenceRepository.currencyStream.first().toRight()
    }

}
