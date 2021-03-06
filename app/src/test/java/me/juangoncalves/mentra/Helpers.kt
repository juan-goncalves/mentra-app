package me.juangoncalves.mentra

import either.Either
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.IconType
import me.juangoncalves.mentra.domain_layer.models.Price
import java.time.LocalDateTime
import java.util.*

typealias Right<T> = Either.Right<T>
typealias Left<T> = Either.Left<T>

internal val USD: Currency = Currency.getInstance("USD")

internal val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png", IconType.Unknown)
internal val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png", IconType.Unknown)
internal val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png", IconType.Unknown)


internal fun Double.toPrice(
    currency: Currency = USD,
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(toBigDecimal(), currency, timestamp)

internal fun <T> T.toRight(): Right<T> = Right(this)

internal infix fun Int.at(timestamp: LocalDateTime): Price =
    this.toDouble().toPrice(timestamp = timestamp)