package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import javax.inject.Inject

class GetCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<Unit, List<Coin>> {

    override suspend operator fun invoke(params: Unit): Either<Failure, List<Coin>> {
        return coinRepository.getCoins()
    }

}