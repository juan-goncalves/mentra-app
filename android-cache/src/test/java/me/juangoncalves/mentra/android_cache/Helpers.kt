package me.juangoncalves.mentra.android_cache

import kotlinx.datetime.toKotlinLocalDateTime
import me.juangoncalves.mentra.android_cache.models.CoinModel
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.time.LocalDateTime
import java.util.*


internal val USD: Currency = Currency.getInstance("USD")
internal val EUR: Currency = Currency.getInstance("EUR")

internal val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png", 1)
internal val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png", 2)
internal val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png", 3)

internal val BitcoinModel = CoinModel(
    "BTC",
    "http://url.com/btc.png",
    "Bitcoin",
    position = 1,
)
internal val EthereumModel = CoinModel(
    "ETH",
    "http://url.com/eth.png",
    "Ethereum",
    position = 2,
)
internal val RippleModel = CoinModel(
    "XRP",
    "http://url.com/xrp.png",
    "Ripple",
    position = 3,
)

internal fun Double.toPrice(
    currency: Currency = USD,
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(toBigDecimal(), currency, timestamp.toKotlinLocalDateTime())
