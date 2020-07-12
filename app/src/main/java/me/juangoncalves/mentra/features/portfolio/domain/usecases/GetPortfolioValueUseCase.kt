package me.juangoncalves.mentra.features.portfolio.domain.usecases

import either.Either
import either.fold
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import java.time.LocalDateTime

class GetPortfolioValueUseCase(
    private val getWallets: GetWalletsUseCase,
    private val getCoinPrice: GetCoinPriceUseCase
) {

    suspend operator fun invoke(currency: Currency = Currency.USD): Either<Failure, Price> {
        return when (val walletsResult = getWallets()) {
            is Either.Left -> walletsResult
            is Either.Right -> {
                val sum = walletsResult.value.sumByDouble { wallet ->
                    getCoinPrice(wallet.coin, currency).fold(
                        left = { 0.0 },
                        right = { it.value * wallet.amount }
                    )
                }
                Either.Right(Price(currency, sum, LocalDateTime.now()))
            }
        }
    }

}