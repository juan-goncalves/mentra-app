package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import javax.inject.Inject

class GetCoinsUseCase @Inject constructor(private val coinRepository: CoinRepository) {

    suspend operator fun invoke(): Either<Failure, List<Coin>> = coinRepository.getCoins()

}