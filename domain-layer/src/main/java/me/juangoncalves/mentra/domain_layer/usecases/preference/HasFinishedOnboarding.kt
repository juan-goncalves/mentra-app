package me.juangoncalves.mentra.domain_layer.usecases.preference

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.repositories.PreferenceRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidUseCase
import javax.inject.Inject

class HasFinishedOnboarding @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : VoidUseCase<Boolean> {

    override suspend fun invoke(): Either<Failure, Boolean> {
        return preferenceRepository.hasFinishedOnboarding()
    }

}