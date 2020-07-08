package me.juangoncalves.mentra

import either.Either
import me.juangoncalves.mentra.features.portfolio.data.models.CoinModel
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Currency
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price
import java.time.LocalDateTime

typealias Right<T> = Either.Right<T>
typealias Left<T> = Either.Left<T>

val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png")
val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png")
val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png")

val USDPrices = mapOf(
    Bitcoin to Price(Currency.USD, 9538.423, LocalDateTime.now()),
    Ethereum to Price(Currency.USD, 242.351, LocalDateTime.now()),
    Ripple to Price(Currency.USD, 0.2987, LocalDateTime.now())
)

val BitcoinModel = CoinModel("BTC", "http://url.com/btc.png", "Bitcoin")
val EthereumModel = CoinModel("ETH", "http://url.com/eth.png", "Ethereum")
val RippleModel = CoinModel("XRP", "http://url.com/xrp.png", "Ripple")
