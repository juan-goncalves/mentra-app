package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject


class GetCoins @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<Unit, List<Coin>> {

    override suspend operator fun invoke(params: Unit): Either<OldFailure, List<Coin>> {
        TODO()
//        return coinRepository.getCoins()
    }

}