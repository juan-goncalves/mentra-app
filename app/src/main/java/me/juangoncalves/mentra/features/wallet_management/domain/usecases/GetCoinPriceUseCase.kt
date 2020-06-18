package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Money
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.CoinRepository

class GetCoinPriceUseCase(private val coinRepository: CoinRepository) {

    suspend fun execute(coin: Coin, currency: Currency = Currency.USD): Either<Failure, Money> =
        coinRepository.getCoinPrice(coin, currency)

}