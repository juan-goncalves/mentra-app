package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import javax.inject.Inject

class GetCoinPrice @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<Coin, Price> {

    override suspend operator fun invoke(params: Coin): Either<Failure, Price> {
        return coinRepository.getCoinPrice(params)
    }

}