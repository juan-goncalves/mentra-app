package me.juangoncalves.mentra

import either.Either
import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.extensions.now
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*

typealias Right<T> = Either.Right<T>
typealias Left<T> = Either.Left<T>

internal val USD: Currency = Currency.getInstance("USD")

internal val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png", 1)
internal val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png", 2)
internal val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png", 3)


internal fun Double.toPrice(
    currency: Currency = USD,
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(toBigDecimal(), currency, timestamp)

internal fun <T> T.toRight(): Right<T> = Right(this)

internal infix fun Int.at(timestamp: LocalDateTime): Price =
    this.toDouble().toPrice(timestamp = timestamp)