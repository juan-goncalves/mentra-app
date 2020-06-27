package me.juangoncalves.mentra.features.portfolio.domain.usecases

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.repositories.CoinRepository

class GetCoinsUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(): Either<Failure, List<Coin>> = coinRepository.getCoins()

}