package me.juangoncalves.mentra.data_layer

import kotlinx.datetime.LocalDateTime
import me.juangoncalves.mentra.domain_layer.extensions.now
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.IconType
import me.juangoncalves.mentra.domain_layer.models.Price
import java.util.*

internal val USD: Currency = Currency.getInstance("USD")
internal val EUR: Currency = Currency.getInstance("EUR")
internal val CAD: Currency = Currency.getInstance("CAD")

internal val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png", IconType.Unknown, 1)
internal val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png", IconType.Unknown, 2)
internal val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png", IconType.Unknown, 3)

internal fun Double.toPrice(
    currency: Currency = USD,
    timestamp: LocalDateTime = LocalDateTime.now()
): Price = Price(toBigDecimal(), currency, timestamp)
