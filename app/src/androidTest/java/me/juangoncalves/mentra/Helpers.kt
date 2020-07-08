package me.juangoncalves.mentra

import me.juangoncalves.mentra.core.db.models.CoinModel
import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin

object InstrumentationHelpers {
    val Bitcoin = Coin("Bitcoin", "BTC", "http://url.com/btc.png")
    val Ethereum = Coin("Ethereum", "ETH", "http://url.com/eth.png")
    val Ripple = Coin("Ripple", "XRP", "http://url.com/xrp.png")

    val BitcoinModel = CoinModel(
        "BTC",
        "http://url.com/btc.png",
        "Bitcoin"
    )
    val EthereumModel = CoinModel(
        "ETH",
        "http://url.com/eth.png",
        "Ethereum"
    )
    val RippleModel = CoinModel(
        "XRP",
        "http://url.com/xrp.png",
        "Ripple"
    )
}

