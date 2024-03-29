package me.juangoncalves.mentra.domain_layer

import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.extensions.now
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*


internal val USD: Currency = Currency.getInstance("USD")
internal val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png", 1)
internal val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png", 2)
internal val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png", 3)

internal val USDPrices = mapOf(
    Bitcoin to Price(9538.423.toBigDecimal(), USD, LocalDateTime.now()),
    Ethereum to Price(242.351.toBigDecimal(), USD, LocalDateTime.now()),
    Ripple to Price(0.2987.toBigDecimal(), USD, LocalDateTime.now())
)

internal fun Double.toPrice(
    currency: Currency = USD,
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(toBigDecimal(), currency, timestamp)

internal infix fun Int.at(timestamp: LocalDateTime): Price =
    this.toDouble().toPrice(timestamp = timestamp)