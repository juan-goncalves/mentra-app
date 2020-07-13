package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository

class GetCoinPriceUseCase(private val coinRepository: CoinRepository) {

    suspend operator fun invoke(
        coin: Coin,
        currency: Currency = Currency.USD
    ): Either<Failure, Price> = coinRepository.getCoinPrice(coin, currency)

}