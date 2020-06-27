package me.juangoncalves.mentra.features.wallet_management

import either.Either
import me.juangoncalves.mentra.features.wallet_management.data.models.CoinModel
import me.juangoncalves.mentra.features.wallet_management.data.schemas.CoinSchema
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Currency
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price
import java.time.LocalDateTime

typealias Right<T> = Either.Right<T>
typealias Left<T> = Either.Left<T>

val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.jpg")
val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.jpg")
val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.jpg")

val Now: LocalDateTime = LocalDateTime.of(2020, 6, 27, 12, 30, 30)
val OneMinuteAgo: LocalDateTime = Now.minusMinutes(1)
val TwoHoursAgo: LocalDateTime = Now.minusHours(2)

val USDPrices = mapOf(
    Bitcoin to Price(Currency.USD, 9538.423, Now),
    Ethereum to Price(Currency.USD, 242.351, Now),
    Ripple to Price(Currency.USD, 0.2987, Now)
)

val BitcoinModel = CoinModel("BTC", "http://url.com/btc.jpg", "Bitcoin")
val EthereumModel = CoinModel("ETH", "http://url.com/eth.jpg", "Ethereum")
val RippleModel = CoinModel("XRP", "http://url.com/xrp.jpg", "Ripple")

val BitcoinSchema = CoinSchema(
    "BTC",
    "http://url.com/btc.jpg",
    "Bitcoin",
    false
)
val EthereumSchema = CoinSchema(
    "ETH",
    "http://url.com/eth.jpg",
    "Ethereum",
    false
)
val RippleSchema = CoinSchema(
    "XRP",
    "http://url.com/xrp.jpg",
    "Ripple",
    false
)
