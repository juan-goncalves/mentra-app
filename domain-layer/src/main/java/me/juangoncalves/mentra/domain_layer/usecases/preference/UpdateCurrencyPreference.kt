package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import java.util.*
import javax.inject.Inject

class UpdateCurrencyPreference @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : UseCase<Currency, Unit> {

    override suspend fun invoke(params: Currency): Either<Failure, Unit> {
        return preferenceRepository.updateCurrencyPreference(params)
    }

}