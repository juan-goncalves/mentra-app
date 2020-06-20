package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import either.fold
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price

class GetPortfolioValueUseCase(
    private val walletsUseCase: GetWalletsUseCase,
    private val priceUseCase: GetCoinPriceUseCase
) {

    suspend fun execute(currency: Currency = Currency.USD): Either<Failure, Price> {
        return when (val walletsResult = walletsUseCase.execute()) {
            is Either.Left -> walletsResult
            is Either.Right -> {
                val sum = walletsResult.value.sumByDouble { wallet ->
                    priceUseCase.execute(wallet.coin, currency).fold(
                        left = { 0.0 },
                        right = { it.value * wallet.amount }
                    )
                }
                Either.Right(Price(currency, sum))
            }
        }
    }

}