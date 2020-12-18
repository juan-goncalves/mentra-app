package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class GetCoinPrice @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<Coin, Price> {

    override suspend operator fun invoke(params: Coin): Either<OldFailure, Price> {
        return coinRepository.getCoinPrice(params)
    }

}