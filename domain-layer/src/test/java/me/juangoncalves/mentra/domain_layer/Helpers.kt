package me.juangoncalves.mentra.domain_layer

import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.IconType
import me.juangoncalves.mentra.domain_layer.models.Price
import java.time.LocalDateTime
import java.util.*


val USD: Currency = Currency.getInstance("USD")
val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png", IconType.Unknown)
val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png", IconType.Unknown)
val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png", IconType.Unknown)

val USDPrices = mapOf(
    Bitcoin to Price(9538.423.toBigDecimal(), USD, LocalDateTime.now()),
    Ethereum to Price(242.351.toBigDecimal(), USD, LocalDateTime.now()),
    Ripple to Price(0.2987.toBigDecimal(), USD, LocalDateTime.now())
)

fun Double.toPrice(
    currency: Currency = USD,
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(toBigDecimal(), currency, timestamp)

infix fun Int.at(timestamp: LocalDateTime): Price = this.toDouble().toPrice(timestamp = timestamp)