package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidInteractor
import javax.inject.Inject


class GetCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : VoidInteractor<List<Coin>> {

    override suspend fun invoke(): Either<Failure, List<Coin>> {
        return coinRepository.getCoins()
    }

}