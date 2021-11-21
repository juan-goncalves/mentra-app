package me.juangoncalves.mentra.android_network

import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.IconType
import java.util.*

internal val USD: Currency = Currency.getInstance("USD")

internal val Bitcoin =
    Coin("Bitcoin", "BTC", "https://www.cryptocompare.com/btc.png", IconType.Unknown)
internal val Ethereum =
    Coin("Ethereum", "ETH", "https://www.cryptocompare.com/eth.png", IconType.Unknown)
internal val Ripple =
    Coin("Ripple", "XRP", "https://www.cryptocompare.com/xrp.png", IconType.Unknown)
