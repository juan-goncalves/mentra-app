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

val USDPrices = mapOf(
    Bitcoin to Price(Currency.USD, 9538.423, LocalDateTime.now()),
    Ethereum to Price(Currency.USD, 242.351, LocalDateTime.now()),
    Ripple to Price(Currency.USD, 0.2987, LocalDateTime.now())
)

val EURPrices = mapOf(
    Bitcoin to Price(Currency.EUR, 8300.423, LocalDateTime.now()),
    Ethereum to Price(Currency.EUR, 210.351, LocalDateTime.now()),
    Ripple to Price(Currency.EUR, 0.163, LocalDateTime.now())
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
