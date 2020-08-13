package me.juangoncalves.mentra.domain.usecases

import either.Either
import either.fold
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import java.time.LocalDateTime
import javax.inject.Inject

class GetPortfolioValueUseCase @Inject constructor(
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