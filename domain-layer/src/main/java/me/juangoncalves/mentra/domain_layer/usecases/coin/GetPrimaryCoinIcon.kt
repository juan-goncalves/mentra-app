package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import either.fold
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.IconRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class GetPrimaryCoinIcon @Inject constructor(
    private val iconRepository: IconRepository
) : UseCase<Coin, String> {

    override suspend fun invoke(params: Coin): Either<Failure, String> = withContext(IO) {
        iconRepository.getAlternativeIconFor(params)
            .fold(
                left = { params.imageUrl },
                right = { it ?: params.imageUrl }
            )
            .toRight()
    }
}