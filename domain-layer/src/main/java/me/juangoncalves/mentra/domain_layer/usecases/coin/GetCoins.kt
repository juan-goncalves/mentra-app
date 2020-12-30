package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidUseCase
import javax.inject.Inject

class GetCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : VoidUseCase<List<Coin>> {

    override suspend fun invoke(): Either<Failure, List<Coin>> {
        return coinRepository.getCoins()
    }

}