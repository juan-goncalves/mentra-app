package me.juangoncalves.mentra.android_network

import me.juangoncalves.mentra.domain_layer.models.Coin
import java.util.*

internal val USD: Currency = Currency.getInstance("USD")

internal val Bitcoin = Coin("Bitcoin", "BTC", "https://www.cryptocompare.com/btc.png", 1)
internal val Ethereum = Coin("Ethereum", "ETH", "https://www.cryptocompare.com/eth.png", 2)
internal val Ripple = Coin("Ripple", "XRP", "https://www.cryptocompare.com/xrp.png", 3)
