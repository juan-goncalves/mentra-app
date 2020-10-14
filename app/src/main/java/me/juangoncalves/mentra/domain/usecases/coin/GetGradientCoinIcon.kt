package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.Right
import java.util.*
import javax.inject.Inject

class GetGradientCoinIcon @Inject constructor() : UseCase<GetGradientCoinIcon.Params, String> {

    data class Params(val coin: Coin, val size: Int = 200)

    override suspend fun invoke(params: Params): Either<Failure, String> {
        val (coin, size) = params
        val symbol = coin.symbol.toLowerCase(Locale.ROOT)
        return Right("https://cryptoicons.org/api/icon/$symbol/$size")
    }

}