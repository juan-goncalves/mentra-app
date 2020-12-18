package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import java.util.*
import javax.inject.Inject


class GetGradientCoinIcon @Inject constructor() : UseCase<GetGradientCoinIcon.Params, String> {

    data class Params(val coin: Coin, val size: Int = 200)

    override suspend fun invoke(params: Params): Either<OldFailure, String> {
        val (coin, size) = params
        val symbol = coin.symbol.toLowerCase(Locale.ROOT)
        return Right("https://cryptoicons.org/api/icon/$symbol/$size")
    }

}