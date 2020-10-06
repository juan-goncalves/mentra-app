package me.juangoncalves.mentra.domain.usecases.coin

import me.juangoncalves.mentra.domain.models.Coin
import java.util.*
import javax.inject.Inject

class GetGradientCoinIconUseCase @Inject constructor() {

    operator fun invoke(coin: Coin, size: Int = 200): String {
        val symbol = coin.symbol.toLowerCase(Locale.ROOT)
        return "https://cryptoicons.org/api/icon/$symbol/$size"
    }

}