package me.juangoncalves.mentra.features.wallet_management

import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Money

val Bitcoin = Coin(name = "Bitcoin", symbol = "BTC", imageUrl = "http://url.com/btc.jpg")
val Ethereum = Coin(name = "Ethereum", symbol = "ETH", imageUrl = "http://url.com/eth.jpg")
val Ripple = Coin(name = "Ripple", symbol = "XRP", imageUrl = "http://url.com/xrp.jpg")

val USDPrices = mapOf(
    Bitcoin to Money(Currency.USD, 9538.423),
    Ethereum to Money(Currency.USD, 242.351),
    Ripple to Money(Currency.USD, 0.2987)
)