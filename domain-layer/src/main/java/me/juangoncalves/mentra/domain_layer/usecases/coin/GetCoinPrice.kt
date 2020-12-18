package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.Interactor
import javax.inject.Inject

class GetCoinPrice @Inject constructor(
    private val coinRepository: CoinRepository
) : Interactor<Coin, Price> {

    override suspend operator fun invoke(params: Coin): Either<Failure, Price> {
        return coinRepository.getCoinPrice(params)
    }

}