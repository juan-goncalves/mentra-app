package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository

class GetCoinPricesUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(
        coins: List<Coin>,
        currency: Currency = Currency.USD
    ): Either<Failure, Map<Coin, Double>> = coinRepository.getCoinPrices(coins, currency)

}