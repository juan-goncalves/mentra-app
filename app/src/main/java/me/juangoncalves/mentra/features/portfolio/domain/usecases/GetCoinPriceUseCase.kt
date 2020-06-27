package me.juangoncalves.mentra.features.portfolio.domain.usecases

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import me.juangoncalves.mentra.features.portfolio.domain.repositories.CoinRepository

class GetCoinPriceUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(coin: Coin, currency: Currency = Currency.USD): Either<Failure, Price> =
        coinRepository.getCoinPrice(coin, currency)

}