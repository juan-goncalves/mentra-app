package me.juangoncalves.mentra.android_network.sources

import me.juangoncalves.mentra.android_network.services.CryptoIconsService
import me.juangoncalves.mentra.data_layer.sources.coin.CoinIconDataSource
import me.juangoncalves.mentra.domain_layer.models.Coin
import java.util.*
import javax.inject.Inject

class CryptoIconsDataSource @Inject constructor(
    private val iconService: CryptoIconsService
) : CoinIconDataSource {

    override suspend fun getAlternativeIconFor(coin: Coin): String? {
        val symbol = coin.symbol.toLowerCase(Locale.ROOT)

        val gradientIconCheck = iconService.checkGradientIconAvailability(symbol)

        return if (gradientIconCheck.isSuccessful) {
            gradientIconCheck.raw().request.url.toString()
        } else {
            null
        }
    }

}